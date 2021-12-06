package me.damianciepiela.server;

import me.damianciepiela.Connection;
import me.damianciepiela.LoggerAdapter;
import me.damianciepiela.Question;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

// TODO: change to callable
// TODO: probably remove check connection to rely on try catch blocks
public class ServerClient implements Runnable, Connection {

    private final LoggerAdapter logger;
    private final List<Question> questions;

    private final Socket socket;
    private final DataInputStream inFromClient;
    private final DataOutputStream outToClient;

    private volatile ClientConnectionEvent connection;
    private int score = 0;

    private String id;
    private String name;
    private String surrname;

    public interface Observer {
        void update(ClientConnectionEvent connectionEvent);
    }
    private final Observer observer;

    public ServerClient(Socket socket, LoggerAdapter logger, List<Question> questions, Observer observer) throws IOException {
        this.socket = socket;
        this.logger = logger;
        this.questions = questions;
        this.inFromClient = new DataInputStream(socket.getInputStream());
        this.outToClient = new DataOutputStream(socket.getOutputStream());
        this.connection = ClientConnectionEvent.ALIVE;
        this.observer = observer;
        this.logger.info("Client created");
    }

    public String getFrom() throws IOException {
        String fromClient = Connection.getFromSource(this.inFromClient);
        this.logger.debug("Got content from Client on " + this.socket.getInetAddress() + ": " + fromClient);
        return fromClient;
    }

    public void sendTo(String text) throws IOException {
        this.logger.debug("Sending to content to Client on " + this.socket.getInetAddress() + ": " + text);
        Connection.sendToSource(this.outToClient, text);
    }

   public void getIdentity() {
       this.logger.info("Getting identity from client...");
       try {
           this.id = getFrom();
           this.name = getFrom();
           this.surrname = getFrom();
       } catch (IOException e) {
           this.logger.error(e);
           changeConncetionAndUpdate(ClientConnectionEvent.LOST);
       }
   }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurrname() {
        return surrname;
    }

    public ClientConnectionEvent getConnection() {
        return connection;
    }

    private void changeConncetionAndUpdate(ClientConnectionEvent connectionEvent) {
        this.connection = connectionEvent;
        this.logger.info("Client connection changed to: " + connectionEvent);
        this.observer.update(connection);
    }

    public void quit() throws IOException {
        this.inFromClient.close();
        this.outToClient.close();
        this.socket.close();
        changeConncetionAndUpdate(ClientConnectionEvent.DISCONENCTED);
        this.logger.info("Client connection closed");
        this.logger.debug("Client ID: " + this.id + " score: " + this.score + " / " + this.questions.size());
    }

    @Override
    public void run() {
        try{
            sendQuestionCount();
            for(Question question : this.questions) {
                showQuestion(question);
                getAnswerAndCheck(question);
            }
            sendScore();
            quit();
            return;
        } catch (IOException e) {
            this.logger.error(e);
        }
        changeConncetionAndUpdate(ClientConnectionEvent.LOST);
    }

    public void sendScore() throws IOException {
        checkConnection();
        sendTo(this.score + " / " + this.questions.size());
    }

    public void sendQuestionCount() throws IOException {
        checkConnection();
        sendTo(String.valueOf(this.questions.size()));
    }

    public void showQuestion(Question question) throws IOException {
        checkConnection();
        sendTo(question.getDescription());
        for(Map.Entry<String, String> entry: question.getAnswers().entrySet()) {
            sendTo(entry.getKey());
            sendTo(entry.getValue());
        }
    }

    public void getAnswerAndCheck(Question question) throws IOException {
        checkConnection();
        String answer = getFrom();
        if (!question.getAnswers().containsKey(answer)) throw new IOException();
        if(question.getCorrectAnswer().equals(answer)) this.score++;
    }

    private void checkConnection() throws IOException {
        boolean connectionStatus = Connection.checkIfSourceIsActive(this.outToClient, this.inFromClient);
        //this.logger.debug("Client on " + this.socket.getInetAddress() + " connection status: " + this.connected);
        if(!connectionStatus) changeConncetionAndUpdate(ClientConnectionEvent.LOST);
    }
}
