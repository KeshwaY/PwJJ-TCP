package me.damianciepiela.server;
import me.damianciepiela.Closable;
import me.damianciepiela.LoggerAdapter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Closable {

    private final LoggerAdapter logger;
    private final ServerSocket serverSocket;

    private final ExecutorService executorService;


    public Server(int port, LoggerAdapter logger, int capacity) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.logger = logger;
        this.executorService = Executors.newFixedThreadPool(capacity);
        this.logger.debug("Server created.");
    }

    public void start() {
        this.logger.info("Server starting on port: " + this.serverSocket.getLocalPort() + "...");
        this.logger.info("Server waiting for connections...");
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                this.logger.info("Client connected, address: " + socket.getInetAddress());
                ServerClient client = new ServerClient(socket);
                System.out.println(client.getFrom());
                client.sendTo("ASD");
            } catch (IOException e) {
               this.logger.error(e);
            }
        }
    }

    public void close() throws IOException {
        this.serverSocket.close();
    }

}
