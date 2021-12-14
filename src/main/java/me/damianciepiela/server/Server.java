package me.damianciepiela.server;

import me.damianciepiela.Closable;
import me.damianciepiela.Connection;
import me.damianciepiela.ConnectionStatus;
import me.damianciepiela.LoggerAdapter;

import javax.xml.crypto.Data;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Server implements Closable, ReadQuestions {

    private final LoggerAdapter logger;
    private final DatagramSocket serverSocket;

    private List<Question> questions;

    private final Map<SocketAddress, ServerClient> clients;
    private final int capacity;

    private final File answersDatabase;
    private final File scoresDatabase;

    public Server(int port, LoggerAdapter logger, int capacity, File answersDatabase, File scoresDatabase) throws IOException {
        this.serverSocket = new DatagramSocket(port);
        this.logger = logger;
        this.clients = new HashMap<>();
        this.capacity = capacity;
        this.answersDatabase = answersDatabase;
        this.scoresDatabase = scoresDatabase;
        this.logger.info("Server created.");
    }

    public void loadQuestions(String fileName) throws QuestionFormattingException, IOException {
        this.questions = ReadQuestions.loadQuestionsFromFile(fileName);
        this.logger.debug("Questions from file: " + fileName + " loaded");
    }

    public void start() {
        this.logger.info("Server starting on port: " + this.serverSocket.getLocalPort() + "...");
        this.logger.info("Server waiting for connections...");
        while (!serverSocket.isClosed()) {
            try {
                byte[] receiveData = new byte[256];
                byte[] sendData = new byte[256];

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String connectionStatus = new String(receivePacket.getData(), 0, receivePacket.getLength());

                if(this.clients.containsKey(receivePacket.getSocketAddress())) {
                    handleClient(this.clients.get(receivePacket.getSocketAddress()), connectionStatus);
                    continue;
                }

                if(ConnectionStatus.NEW_CONNECTION.name().equals(connectionStatus)) {

                    if(this.clients.size() == this.capacity) continue;
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());

                    this.serverSocket.send(sendPacket);

                    this.clients.put(receivePacket.getSocketAddress(), createNewClient(receivePacket, sendPacket));
                    this.logger.info("Client connected, address: " + receivePacket.getSocketAddress());
                }
            } catch (IOException e) {
               this.logger.error(e);
            }
        }
    }

    private ServerClient createNewClient(DatagramPacket receivePacket, DatagramPacket sendPacket) throws IOException {
        return new ServerClient(receivePacket, sendPacket, new LoggerAdapter(ServerClient.class), this.questions);
    }

    private void handleClient(ServerClient serverClient, String connection) throws IOException {
        if(ConnectionStatus.SENDING_ID.name().equals(connection)) {
           recognizeRequest(serverClient);
           String id = getFromClient(serverClient);
           informClientDataReceived(serverClient);
           String name = getFromClient(serverClient);
           informClientDataReceived(serverClient);
           String surname = getFromClient(serverClient);
           informClientDataReceived(serverClient);

           serverClient.setId(id);
           serverClient.setName(name);
           serverClient.setSurname(surname);

           informClientDataReceived(serverClient);
        }
        if(ConnectionStatus.WAITING_FOR_QUESTIONS_COUNT.name().equals(connection)) {
            recognizeRequest(serverClient);
            sendToClient(serverClient, String.valueOf(this.questions.size()));
        }
        if(ConnectionStatus.WAITING_FOR_QUESTION.name().equals(connection)) {
            recognizeRequest(serverClient);
            int numberOfQuestion = Integer.parseInt(getFromClient(serverClient));
            Question question = this.questions.get(numberOfQuestion);
            informClientDataReceived(serverClient);
            sendToClient(serverClient, question.description());
            for(Map.Entry<String, String> entry : question.answers().entrySet()) {
                sendToClient(serverClient, entry.getKey());
                sendToClient(serverClient, entry.getValue());
            }
        }
        if(ConnectionStatus.SENDING_ANSWER.name().equals(connection)) {
            recognizeRequest(serverClient);
            int numberOfQuestion = Integer.parseInt(getFromClient(serverClient));
            Question question = this.questions.get(numberOfQuestion);
            informClientDataReceived(serverClient);
            String answer = getFromClient(serverClient);
            serverClient.getAnswerAndCheck(question, answer);
            informClientDataReceived(serverClient);
        }
        if(ConnectionStatus.WAITING_FOR_SCORE.name().equals(connection)) {
            recognizeRequest(serverClient);
            String totalScore = serverClient.getScore() + " / " + this.questions.size();
            sendToClient(serverClient, totalScore);
        }
        if(ConnectionStatus.END.name().equals(connection)) {
            recognizeRequest(serverClient);
            saveClientExam(serverClient);
            this.clients.remove(serverClient.getReceivePacket().getSocketAddress());
        }
    }

    private String getFromClient(ServerClient client) throws IOException {
        this.serverSocket.receive(client.getReceivePacket());
        return new String(client.getReceivePacket().getData(), 0, client.getReceivePacket().getLength());
    }

    private void recognizeRequest(ServerClient client) throws IOException {
        sendToClient(client, ConnectionStatus.REQUEST_RECOGNIZED.name());
    }

    private void informClientDataReceived(ServerClient client) throws IOException {
        sendToClient(client, ConnectionStatus.DATA_RECEIVED.name());
    }

    private void sendToClient(ServerClient client, String text) throws IOException {
        Connection.sendToClient(this.serverSocket,  client.getSendPacket(), text);
    }

    private void saveClientExam(ServerClient client) {
        ClientAnswers finalScore = client.getClientAnswers();
        writeToFile(this.scoresDatabase, finalScore.clientId() + ": " + finalScore.score());
        StringBuilder stringBuilder = new StringBuilder(finalScore.clientId() + ":\n");
        for(String answer : finalScore.answers()) {
            stringBuilder.append(" ").append(answer).append("\n");
        }
        writeToFile(this.answersDatabase, stringBuilder.toString());
    }

    private static void writeToFile(File file, String line) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        this.serverSocket.close();
    }

}
