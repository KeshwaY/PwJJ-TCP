package me.damianciepiela.server;

import me.damianciepiela.Connection;
import me.damianciepiela.LoggerAdapter;
import me.damianciepiela.Question;

import java.io.*;
import java.net.Socket;
import java.util.List;

// TODO: change to callable
public class ServerClient implements Runnable, Connection {

    private final LoggerAdapter logger;
    private final List<Question> questions;

    private final Socket socket;
    private final DataInputStream inFromClient;
    private final DataOutputStream outToClient;

    private volatile boolean connected;
    private volatile int score = 0;

    private String id;
    private String name;
    private String surrname;

    public interface Observer {
        void update(String event);
    }
    private Observer observer;

    public ServerClient(Socket socket, LoggerAdapter logger, List<Question> questions, Observer observer) throws IOException {
        this.socket = socket;
        this.logger = logger;
        this.questions = questions;
        this.inFromClient = new DataInputStream(socket.getInputStream());
        this.outToClient = new DataOutputStream(socket.getOutputStream());
        this.connected = true;
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

    public void setId(String id) {
        this.logger.debug("Changing id to: " + id);
        this.id = id;
    }

    public void setName(String name) {
        this.logger.debug("Changing name to: " + name);
        this.name = name;
    }

    public void setSurname(String surname) {
        this.logger.debug("Changing surname to: " + surname);
        this.surrname = surname;
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

    public void quit() throws IOException {
        this.inFromClient.close();
        this.outToClient.close();
        this.socket.close();
        this.logger.info("Client connection closed");
    }

    @Override
    public void run() {
        while (connected) {
            try {
                checkConnection();
            } catch (IOException e) {
                this.logger.error(e);
                break;
            }
        }
        observer.update("Close connection");
    }

    private void checkConnection() throws IOException {
        this.connected = Connection.checkIfSourceIsActive(this.outToClient, this.inFromClient);
        //this.logger.debug("Client on " + this.socket.getInetAddress() + " connection status: " + this.connected);
        if(!connected) throw new IOException();
    }
}
