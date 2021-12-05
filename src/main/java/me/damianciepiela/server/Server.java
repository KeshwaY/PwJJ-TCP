package me.damianciepiela.server;
import me.damianciepiela.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server implements Closable, ReadQuestions {

    private final LoggerAdapter logger;
    private final ServerSocket serverSocket;

    private final ThreadManager threadManager;
    private List<Question> questions;

    public Server(int port, LoggerAdapter logger, ThreadManager threadManager) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.logger = logger;
        this.threadManager = threadManager;
        this.logger.info("Server created.");
    }

    public void loadQuestions(String fileName) throws QuestionFormattingException, IOException {
        this.questions = ReadQuestions.loadQuestionsFromFile(fileName);
        this.logger.debug("Questions from file: " + fileName + " loaded");
    }

    public void start() {
        this.logger.info("Server starting on port: " + this.serverSocket.getLocalPort() + "...");
        this.logger.info("Server waiting for connections...");
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                this.logger.info("Client connected, address: " + socket.getInetAddress());
                ServerClient client = this.threadManager.createClient(socket, this.questions);
                client.getIdentity();
                if(!client.getConnection().equals(ClientConnectionEvent.ALIVE)) continue;
                this.logger.info("Client connection established");
                this.threadManager.execute(client);
            } catch (IOException e) {
               this.logger.error(e);
            }
        }
    }

    public void close() throws IOException {
        this.serverSocket.close();
    }

}
