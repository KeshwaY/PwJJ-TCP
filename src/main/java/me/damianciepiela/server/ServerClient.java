package me.damianciepiela.server;

import me.damianciepiela.Connection;
import me.damianciepiela.ConnectionStatus;
import me.damianciepiela.LoggerAdapter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

// TODO: probably remove check connection to rely on try catch blocks
public class ServerClient implements Callable<ClientAnswers> {

    private final LoggerAdapter logger;
    private final List<Question> questions;

    private final DatagramPacket receivePacket;
    private final DatagramPacket sendPacket;
    private final DatagramSocket serverSocket;

    private volatile ConnectionStatus connection;
    private int score = 0;

    private final List<String> answers = new ArrayList<>();

    private String id;
    private String name;
    private String surname;

    public interface Observer {
        void update(ConnectionStatus connectionEvent);
    }
    private final Observer observer;

    public ServerClient(DatagramSocket serverSocket, DatagramPacket receivePacket, DatagramPacket sendPacket, LoggerAdapter logger, List<Question> questions, Observer observer) throws IOException {
        this.receivePacket = receivePacket;
        this.sendPacket = sendPacket;
        this.serverSocket = serverSocket;
        this.logger = logger;
        this.questions = questions;
        this.connection = ConnectionStatus.ALIVE;
        this.observer = observer;
        this.logger.info("Client created");
    }

    public String getFrom() throws IOException, ClassNotFoundException {
        String fromClient = Connection.getServer(this.serverSocket, this.receivePacket, this.sendPacket);
        this.logger.debug("Got content from Client on " + this.receivePacket.getAddress() + ": " + fromClient);
        return fromClient;
    }

    public void sendTo(String text) throws IOException, ClassNotFoundException {
        this.logger.debug("Sending content to the Client on " + this.sendPacket.getAddress() + ": " + text);
        Connection.sendServer(this.serverSocket, this.receivePacket, this.sendPacket, text);
    }

   public void getIdentity() {
       this.logger.info("Getting identity from client...");
       try {
           this.id = getFrom();
           this.name = getFrom();
           this.surname = getFrom();
       } catch (IOException | ClassNotFoundException e) {
           this.logger.error(e);
           changeConnectionAndUpdate(ConnectionStatus.LOST);
       }
   }

    public ConnectionStatus getConnection() {
        return connection;
    }

    private void changeConnectionAndUpdate(ConnectionStatus connectionEvent) {
        this.connection = connectionEvent;
        this.logger.info("Client connection changed to: " + connectionEvent);
        this.observer.update(connection);
    }

    public void quit() throws IOException {
        changeConnectionAndUpdate(ConnectionStatus.DISCONNECTED);
        this.logger.info("Client connection closed");
        this.logger.debug("Client ID: " + this.id + " score: " + this.score + " / " + this.questions.size());
    }

    @Override
    public ClientAnswers call() {
        try{
            getIdentity();
            sendQuestionCount();
            for(Question question : this.questions) {
                showQuestion(question);
                getAnswerAndCheck(question);
            }
            sendScore();
            quit();
            return new ClientAnswers(this.id, this.score, this.answers);
        } catch (IOException | ClassNotFoundException e) {
            this.logger.error(e);
        }
        changeConnectionAndUpdate(ConnectionStatus.LOST);
        return null;
    }

    public void sendScore() throws IOException, ClassNotFoundException {
        //checkConnection();
        sendTo(this.score + " / " + this.questions.size());
    }

    public void sendQuestionCount() throws IOException, ClassNotFoundException {
        //checkConnection();
        sendTo(String.valueOf(this.questions.size()));
    }

    public void showQuestion(Question question) throws IOException, ClassNotFoundException {
        //checkConnection();
        System.out.println("test");
        sendTo(question.description());
        System.out.println("ASD");
        for(Map.Entry<String, String> entry: question.answers().entrySet()) {
            //checkConnection();
            sendTo(entry.getKey());
            //checkConnection();
            sendTo(entry.getValue());
        }
    }

    public void getAnswerAndCheck(Question question) throws IOException, ClassNotFoundException {
        //checkConnection();
        String answer = getFrom();
        if (!question.answers().containsKey(answer)) throw new IOException();
        if(question.correctAnswer().equals(answer)) this.score++;
        this.answers.add(answer);
    }

}
