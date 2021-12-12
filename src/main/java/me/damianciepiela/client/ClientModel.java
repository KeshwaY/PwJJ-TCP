package me.damianciepiela.client;

import me.damianciepiela.Closable;
import me.damianciepiela.Logable;
import me.damianciepiela.LoggerAdapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public final class ClientModel extends Model implements Logable, Closable {

    private final LoggerAdapter logger;

    private final Socket socket;

    private final DataInputStream inFromServer;
    private final DataOutputStream outToServer;

    public ClientModel(LoggerAdapter logger, Socket socket) throws IOException {
        this.logger = logger;
        this.socket = socket;
        this.inFromServer = new DataInputStream(socket.getInputStream());
        this.outToServer = new DataOutputStream(socket.getOutputStream());
    }

    private boolean checkConnectionToServer() throws IOException {
        return checkConnection(this.outToServer, this.inFromServer);
    }

    public void sendToServer(String message) throws IOException {
        boolean connection = checkConnectionToServer();
        if(connection) {
            sendTo(this.outToServer, message);
            this.logger.info("Sending content to the server: " + message);
        }
    }

    public String getFromServer() throws IOException {
        boolean connection = checkConnectionToServer();
        this.logger.info("Connection status: " + connection);
        if(connection) {
            String fromServer = getFrom(this.inFromServer);
            this.logger.info("Got content from the server: " + fromServer);
            return fromServer;
        }
        return null;
    }

    public void sendIdentity(String id, String name, String surname) throws IOException {
        sendTo(this.outToServer, id);
        sendTo(this.outToServer, name);
        sendTo(this.outToServer, surname);
    }

    public Question getQuestion() throws IOException {
        String description = getFromServer();
        Map<String, String> answers = new HashMap<>();
        for(int i = 0; i < 4; i++) {
            String key = getFromServer();
            String value = getFromServer();
            answers.put(key, value);
        }
        return new Question(description, answers);
    }

    public int getQuestionsCount() throws IOException {
        String fromServer = getFromServer();
        if(fromServer == null) throw new IOException();
        return Integer.parseInt(fromServer);
    }

    public String getFinalScore() throws IOException {
        String fromServer = getFromServer();
        if(fromServer == null) throw new IOException();
        return fromServer;
    }

    @Override
    public void close() throws IOException {
        this.socket.close();
        this.outToServer.close();
        this.inFromServer.close();
    }
}
