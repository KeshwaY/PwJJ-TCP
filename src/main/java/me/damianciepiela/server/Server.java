package me.damianciepiela.server;

import me.damianciepiela.Closable;
import me.damianciepiela.ConnectionStatus;
import me.damianciepiela.LoggerAdapter;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server implements Closable, ReadQuestions {

    private final LoggerAdapter logger;
    private final DatagramSocket serverSocket;

    private final ThreadManager threadManager;
    private List<Question> questions;

    private final List<SocketAddress> clients;


    public Server(int port, LoggerAdapter logger, ThreadManager threadManager) throws IOException {
        this.serverSocket = new DatagramSocket(port);
        this.logger = logger;
        this.threadManager = threadManager;
        this.clients = new ArrayList<>();
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
                byte[] receiveData = new byte[256];
                byte[] sendData = new byte[256];

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                if(this.clients.contains(receivePacket.getSocketAddress())) {
                    byte[] existData = "FLAG_PASS".getBytes();
                    this.serverSocket.send(new DatagramPacket(existData, existData.length, receivePacket.getAddress(), receivePacket.getPort()));
                    System.out.println("Exists");
                    continue;
                }

                System.out.println(receivePacket.getAddress());
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());

                this.serverSocket.send(sendPacket);

                System.out.println(receivePacket.getPort());
                System.out.println(receivePacket.getSocketAddress());
                this.clients.add(receivePacket.getSocketAddress());
                this.logger.info("Client connected, address: " + receivePacket.getAddress());

                ServerClient client = this.threadManager.createClient(this.serverSocket, receivePacket, sendPacket, this.questions);
                if(!client.getConnection().equals(ConnectionStatus.ALIVE)) continue;
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
