package me.damianciepiela;

import me.damianciepiela.server.ClientConnectionEvent;
import me.damianciepiela.server.FutureClient;
import me.damianciepiela.server.ServerClient;

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

    private final FileCondition answersDatabase;
    private final FileCondition scoresDatabase;

    public ThreadManager(LoggerAdapter logger, int capacity, FileCondition answersDatabase, FileCondition scoresDatabase) {
        this.logger = logger;
        this.executorService = Executors.newFixedThreadPool(capacity);
        this.capacity = capacity;
        this.answersDatabase = answersDatabase;
        this.scoresDatabase = scoresDatabase;
    }

    public void execute(ServerClient client) {
        this.executorService.submit(new FutureClient(client, this.answersDatabase, this.scoresDatabase));
        this.logger.info("Starting thread for Client: ");
    }

    public ServerClient createClient(Socket socket, List<Question> questionList) throws IOException {
        if(this.capacity < currentSize + 1) {
            this.logger.error("Cloud not create Client object, server is full...");
            return null;
        }
        ServerClient serverClient = new ServerClient(socket, new LoggerAdapter(ServerClient.class), questionList, (event) -> {
            if(event.equals(ClientConnectionEvent.DISCONENCTED) || event.equals(ClientConnectionEvent.LOST)) {
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
