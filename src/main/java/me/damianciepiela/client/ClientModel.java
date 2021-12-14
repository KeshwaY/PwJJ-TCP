package me.damianciepiela.client;

import me.damianciepiela.Closable;
import me.damianciepiela.Logable;
import me.damianciepiela.LoggerAdapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
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
        this.socket.send(sendPacket);
        this.receivePacket = new DatagramPacket(receiveData, receiveData.length);
        this.socket.receive(this.receivePacket);
    }

    public void sendToServer(String message) throws IOException, ClassNotFoundException {
        sendTo(this.socket, this.receivePacket, this.sendPacket, message);
        this.logger.info("Sending content to the server: " + message);
    }

    public String getFromServer() throws IOException, ClassNotFoundException {
        //boolean connection = checkConnectionToServer();
        //this.logger.info("Connection status: " + this.);
        String fromServer = getFrom(this.socket, this.receivePacket, this.sendPacket);
        this.logger.info("Got content from the server: " + fromServer);
        return fromServer;
    }

    public void sendIdentity(String id, String name, String surname) throws IOException, ClassNotFoundException {
        sendTo(this.socket, this.receivePacket, this.sendPacket, id);
        sendTo(this.socket, this.receivePacket, this.sendPacket, name);
        sendTo(this.socket, this.receivePacket, this.sendPacket, surname);
    }

    public Question getQuestion() throws IOException, ClassNotFoundException {
        String description = getFromServer();
        Map<String, String> answers = new HashMap<>();
        for(int i = 0; i < 4; i++) {
            String key = getFromServer();
            String value = getFromServer();
            answers.put(key, value);
        }
        return new Question(description, answers);
    }

    public int getQuestionsCount() throws IOException, ClassNotFoundException {
        String fromServer = getFromServer();
        if(fromServer == null) throw new IOException();
        return Integer.parseInt(fromServer);
    }

    public String getFinalScore() throws IOException, ClassNotFoundException {
        String fromServer = getFromServer();
        if(fromServer == null) throw new IOException();
        return fromServer;
    }

    @Override
    public void close() throws IOException {
        this.socket.close();
    }
}
