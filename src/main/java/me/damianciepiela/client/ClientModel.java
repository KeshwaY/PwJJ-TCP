package me.damianciepiela.client;

import me.damianciepiela.Closable;
import me.damianciepiela.ConnectionStatus;
import me.damianciepiela.Logable;
import me.damianciepiela.LoggerAdapter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public final class ClientModel extends Model implements Logable, Closable {

    private final LoggerAdapter logger;

    private final InetAddress serverAddress;
    private final int port;

    private final DatagramSocket socket;

    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;

    public ClientModel(LoggerAdapter logger, InetAddress serverAddress, int port) throws IOException {
        this.logger = logger;

        this.serverAddress = serverAddress;
        this.port = port;

        this.socket = new DatagramSocket();
        initConnection();
    }

    private void initConnection() throws IOException {
        byte[] receiveData = new byte[256];
        byte[] sendData = new byte[256];

        this.sendPacket = new DatagramPacket(sendData, sendData.length, this.serverAddress, port);
        this.sendPacket.setData(ConnectionStatus.NEW_CONNECTION.name().getBytes());
        this.socket.send(sendPacket);
        this.receivePacket = new DatagramPacket(receiveData, receiveData.length);
        this.socket.receive(this.receivePacket);
        this.sendPacket.setData(sendData);
    }

    private void sendToServer(String message) throws IOException {
        sendTo(this.socket, this.receivePacket, this.sendPacket, message);
        this.logger.info("Sending content to the server: " + message);
    }

    private void sendToServer(ConnectionStatus connectionStatus) throws IOException {
        sendTo(this.socket, this.receivePacket, this.sendPacket, connectionStatus);
        this.logger.info("Sending status to the server: " + connectionStatus.name());
    }

    private String getFromServer() throws IOException {
        String fromServer = getFrom(this.socket, this.receivePacket, this.sendPacket);
        this.logger.info("Got content from the server: " + fromServer);
        return fromServer;
    }

    public void sendIdentity(String id, String name, String surname) throws IOException {
        sendToServer(ConnectionStatus.SENDING_ID);
        sendToServer(id);
        sendToServer(name);
        sendToServer(surname);
    }

    public Question getQuestion(int id) throws IOException {
        sendToServer(ConnectionStatus.WAITING_FOR_QUESTION);
        sendToServer(String.valueOf(id));
        String description = getFromServer();
        Map<String, String> answers = new HashMap<>();
        for(int i = 0; i < 4; i++) {
            String key = getFromServer();
            String value = getFromServer();
            answers.put(key, value);
        }
        return new Question(description, answers);
    }

    public void sendAnswer(int id, String answer) throws IOException {
        sendToServer(ConnectionStatus.SENDING_ANSWER);
        sendToServer(String.valueOf(id));
        sendToServer(answer);
    }

    public int getQuestionsCount() throws IOException {
        sendToServer(ConnectionStatus.WAITING_FOR_QUESTIONS_COUNT);
        String fromServer = getFromServer();
        if(fromServer == null) throw new IOException();
        return Integer.parseInt(fromServer);
    }

    public String getFinalScore() throws IOException {
        sendToServer(ConnectionStatus.WAITING_FOR_SCORE);
        String fromServer = getFromServer();
        if(fromServer == null) throw new IOException();
        return fromServer;
    }

    @Override
    public void close() throws IOException {
        sendToServer(ConnectionStatus.END);
        this.socket.close();
    }
}
