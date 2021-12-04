package me.damianciepiela.server;
import me.damianciepiela.LoggerAdapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final LoggerAdapter logger;
    private final ServerSocket serverSocket;

    public Server(int port, LoggerAdapter logger) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.logger = logger;
        this.logger.debug("Server created.");
    }

    public void start() throws IOException {
        this.logger.info("Server starting on port: " + this.serverSocket.getLocalPort() + "...");
        this.logger.info("Server waiting for connections...");
        while (!serverSocket.isClosed()) {
            Socket client = serverSocket.accept();
            this.logger.info("Client connected, address: " + client.getInetAddress());
            DataInputStream inFromClient = new DataInputStream(client.getInputStream());
            DataOutputStream outToClient = new DataOutputStream(client.getOutputStream());
            this.logger.info("Client response: " + inFromClient.readUTF());
            outToClient.writeUTF("From server");
            outToClient.flush();
            inFromClient.close();
            outToClient.close();
        }
    }

}
