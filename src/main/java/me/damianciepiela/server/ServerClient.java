package me.damianciepiela.server;

import me.damianciepiela.ConnectionStatus;
import me.damianciepiela.LoggerAdapter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

// TODO: probably remove check connection to rely on try catch blocks
public class ServerClient {

    private final LoggerAdapter logger;
    private final List<Question> questions;

    private final DatagramPacket receivePacket;
    private final DatagramPacket sendPacket;

    private final ConnectionStatus connection;

    private int score = 0;

    private final List<String> answers = new ArrayList<>();

    private String id;
    private String name;
    private String surname;


    public ServerClient(DatagramPacket receivePacket, DatagramPacket sendPacket, LoggerAdapter logger, List<Question> questions) throws IOException {
        this.receivePacket = receivePacket;
        this.sendPacket = sendPacket;
        this.logger = logger;
        this.questions = questions;
        this.connection = ConnectionStatus.ALIVE;
        this.logger.info("Client created");
    }

    public DatagramPacket getReceivePacket() {
        return receivePacket;
    }

    public DatagramPacket getSendPacket() {
        return sendPacket;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getScore() {
        return score;
    }

    public ClientAnswers getClientAnswers() {
        return new ClientAnswers(this.id, this.score, this.answers);
    }

    public void getAnswerAndCheck(Question question, String answer) throws IOException {
        //checkConnection();
        //String answer = getFrom();
        if (!question.answers().containsKey(answer)) throw new IOException();
        if(question.correctAnswer().equals(answer)) this.score++;
        this.answers.add(answer);
    }

}
