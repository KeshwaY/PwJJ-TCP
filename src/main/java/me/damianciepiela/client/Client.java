package me.damianciepiela.client;

import me.damianciepiela.LoggerAdapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private final LoggerAdapter logger;
    private final Socket socket;

    public Client(String address, int port, LoggerAdapter logger) throws IOException {
        this.logger = logger;
        this.socket = new Socket(address, port);
        this.logger.debug("Client created.");
    }

    public void test() throws IOException {
        DataOutputStream outToServer = new DataOutputStream(this.socket.getOutputStream());
        outToServer.writeUTF("From client");
        outToServer.flush();
        DataInputStream inFromServer = new DataInputStream(this.socket.getInputStream());
        this.logger.info("Data from server: " + inFromServer.readUTF());
        outToServer.close();
        inFromServer.close();
    }
}
