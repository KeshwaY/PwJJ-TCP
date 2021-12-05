package me.damianciepiela.client;

import me.damianciepiela.Closable;
import me.damianciepiela.Connection;
import me.damianciepiela.LoggerAdapter;

import java.io.*;
import java.net.Socket;

public class Client implements Connection, Closable {
    private final LoggerAdapter logger;
    private final Socket socket;

    private final DataInputStream inFromServer;
    private final DataOutputStream outToServer;

    public Client(String address, int port, LoggerAdapter logger) throws IOException {
        this.logger = logger;
        this.socket = new Socket(address, port);
        this.inFromServer = new DataInputStream(socket.getInputStream());
        this.outToServer = new DataOutputStream(socket.getOutputStream());
        this.logger.debug("Client created.");
    }

    public void test() throws IOException {
        try {
            sendTo("TEST");
            System.out.println(getFrom());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getFrom() throws IOException, ClassNotFoundException {
        return Connection.getFromSource(this.inFromServer);
    }

    public void sendTo(String text) throws IOException {
        Connection.sendToSource(this.outToServer, text);
    }

    public void close() throws IOException {
        this.socket.close();
        this.outToServer.close();
        this.inFromServer.close();
    }
}
