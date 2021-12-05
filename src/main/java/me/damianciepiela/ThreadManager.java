package me.damianciepiela;

import me.damianciepiela.server.ClientConnectionEvent;
import me.damianciepiela.server.ServerClient;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {

    private LoggerAdapter logger;

    private ExecutorService executorService;
    private int capacity;
    private int currentSize = 0;

    public ThreadManager(LoggerAdapter logger, int capacity) {
        this.logger = logger;
        this.executorService = Executors.newFixedThreadPool(capacity);
        this.capacity = capacity;
    }

    public void execute(ServerClient client) {
        this.executorService.execute(client);
        this.logger.info("Starting thread for Client: " + client.getId());
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
