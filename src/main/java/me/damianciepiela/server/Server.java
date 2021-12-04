package me.damianciepiela.server;
import me.damianciepiela.LoggerAdapter;

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
            Socket socket = serverSocket.accept();
            this.logger.info("Client connected, address: " + socket.getInetAddress());
            ServerClient client = new ServerClient(socket);
            System.out.println(client.getFrom());
            client.sendTo("ASD");
        }
    }

    public void close() throws IOException {
        this.serverSocket.close();
    }

}
