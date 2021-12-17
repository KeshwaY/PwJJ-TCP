package me.damianciepiela.server;

import me.damianciepiela.Closable;
import me.damianciepiela.ConnectionStatus;
import me.damianciepiela.LoggerAdapter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class Server implements Closable {

    private final LoggerAdapter logger;
    private final ServerSocket serverSocket;

    private final ThreadManager threadManager;
    private List<Question> questions;

    private final DatabaseConnection databaseConnection;

    public Server(int port, LoggerAdapter logger, ThreadManager threadManager, DatabaseConnection databaseConnection) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.logger = logger;
        this.threadManager = threadManager;
        this.databaseConnection = databaseConnection;
        this.logger.info("Server created.");
    }

    public void loadQuestions(DatabaseConnection databaseConnection) throws SQLException {
        this.questions = databaseConnection.getQuestions();
        this.logger.debug("Questions loaded");
    }

    public void start() {
        this.logger.info("Server starting on port: " + this.serverSocket.getLocalPort() + "...");
        this.logger.info("Server waiting for connections...");
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                this.logger.info("Client connected, address: " + socket.getInetAddress());
                ServerClient client = this.threadManager.createClient(socket, this.questions);
                if(!client.getConnection().equals(ConnectionStatus.ALIVE)) continue;
                this.logger.info("Client connection established");
                this.threadManager.execute(client, this.databaseConnection);
            } catch (IOException e) {
               this.logger.error(e);
            }
        }
    }

    public void close() throws IOException, SQLException {
        this.serverSocket.close();
        this.databaseConnection.close();
    }

}
