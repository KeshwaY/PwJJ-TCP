package me.damianciepiela.server;

import me.damianciepiela.ConnectionStatus;
import me.damianciepiela.LoggerAdapter;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {

    private final LoggerAdapter logger;

    private final ExecutorService executorService;
    private final int capacity;
    private int currentSize = 0;

    public ThreadManager(LoggerAdapter logger, int capacity) {
        this.logger = logger;
        this.executorService = Executors.newFixedThreadPool(capacity);
        this.capacity = capacity;
    }

    public void execute(ServerClient client, DatabaseConnection databaseConnection) {
        this.executorService.submit(new FutureClient(client, databaseConnection));
        this.logger.info("Starting thread for Client: ");
    }

    public ServerClient createClient(Socket socket, List<Question> questionList) throws IOException {
        if(this.capacity < currentSize + 1) {
            this.logger.error("Could not create Client object, server is full...");
            return null;
        }
        ServerClient serverClient = new ServerClient(socket, new LoggerAdapter(ServerClient.class), questionList, (event) -> {
            if(event.equals(ConnectionStatus.DISCONNECTED) || event.equals(ConnectionStatus.LOST)) {
                decreaseCurrentSize();
                logTotalCurrentlyActive();
            }
        });
        this.logger.info("Client object created");
        increaseCurrentSize();
        logTotalCurrentlyActive();
        return serverClient;
    }

    public String getTotalActive() {
        return getCurrentSize() + " / " + getCapacity();
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    public void decreaseCurrentSize() {
        this.logger.debug("Decreasing client active count...");
        this.currentSize--;
    }

    public void increaseCurrentSize() {
        this.logger.debug("Increasing client active count...");
        this.currentSize++;
    }

    public void logTotalCurrentlyActive() {
        this.logger.info("Clients currently active: " + getTotalActive());
    }
}
