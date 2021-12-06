package me.damianciepiela.client;

import me.damianciepiela.Closable;
import me.damianciepiela.Connection;
import me.damianciepiela.LoggerAdapter;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

//TODO: create enum for server connection
public class Client implements Connection, Closable {
    private final LoggerAdapter logger;
    private final Socket socket;

    private boolean connected;

    private final DataInputStream inFromServer;
    private final DataOutputStream outToServer;

    public Client(String address, int port, LoggerAdapter logger) throws IOException {
        this.logger = logger;
        this.socket = new Socket(address, port);
        this.inFromServer = new DataInputStream(socket.getInputStream());
        this.outToServer = new DataOutputStream(socket.getOutputStream());
        this.connected = true;
        this.logger.debug("Connection established");
        this.logger.info("Client created.");
    }

    // TODO: change to bufferedreader?
    public void setIdentity() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Podaj numer albumu: ");
        String id = scanner.next();
        System.out.println("Podaj imie: ");
        String name = scanner.next();
        System.out.println("Podaj nazwisko: ");
        String surname = scanner.next();

        try {
            sendTo(id);
            sendTo(name);
            sendTo(surname);
        } catch (IOException e) {
            close();
        }
    }

    public void start() {
        try {
            setIdentity();
            int questionCount = getQuestionCount();
            while(connected) {
                for(int i = 0; i < questionCount; i++) {
                    System.out.println(getQuestion());
                    Map<String, String> answers = getAnswers();
                    for(Map.Entry<String, String> entry : answers.entrySet()) {
                        System.out.println("[" + entry.getKey() + "] " + entry.getValue());
                    }
                    checkConnection();
                    sendTo("b");
                }
                close();
            }
        } catch (IOException e) {
            this.logger.error(e);
        }
    }

    private String getQuestion() throws IOException {
        checkConnection();
        return getFrom();
    }

    private Map<String, String> getAnswers() throws IOException {
        Map<String, String> questions = new HashMap<>();
        for(int i = 0; i < 4; i++) {
            String key = getFrom();
            String value = getFrom();
            questions.put(key, value);
        }
        if (questions.size() != 4) throw new IOException();
        return questions;
    }

    private int getQuestionCount() throws IOException {
        checkConnection();
        return Integer.parseInt(getFrom());
    }


    public String getFrom() throws IOException {
        String fromServer = Connection.getFromSource(this.inFromServer);
        this.logger.debug("Got content from Server on " + this.socket.getInetAddress() + ": " + fromServer);
        return fromServer;
    }

    public void sendTo(String text) throws IOException {
        this.logger.debug("Sending to content to Server on " + this.socket.getInetAddress() + ": " + text);
        Connection.sendToSource(this.outToServer, text);
    }

    public void checkConnection() throws IOException {
        this.connected = Connection.refreshConnection(this.outToServer, this.inFromServer);
        // TODO: change this to custom exception
        if(!connected) throw new IOException();
        //this.logger.debug("Server on " + this.socket.getInetAddress() + " connection status: " + this.connected);
    }

    public void close() throws IOException {
        this.socket.close();
        this.outToServer.close();
        this.inFromServer.close();
    }
}
