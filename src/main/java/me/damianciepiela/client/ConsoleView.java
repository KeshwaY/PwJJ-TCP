package me.damianciepiela.client;

import me.damianciepiela.Logable;
import me.damianciepiela.LoggerAdapter;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;

public abstract class ConsoleView implements View<String, String>, Logable {

    protected final LoggerAdapter logger;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Future<String> userInput;

    public ConsoleView(LoggerAdapter loggerAdapter) {
        this.logger = loggerAdapter;
    }

    @Override
    public String getUserInput() {
        this.userInput = this.executorService.submit(new ReadUserInputTask());
        String input = null;
        try {
            input = this.userInput.get();
        } catch (ExecutionException | InterruptedException | CancellationException e) {
            this.logger.error(e);
        }
        return input;
    }

    public void cancelWaitingForUserInput() {
        this.userInput.cancel(true);
    }

    @Override
    public void show(String message) {
        System.out.println(message);
        this.logger.info("Printing message to the user: " + message);
    }

    @Override
    public void show(Map<String, String> map) {
        this.logger.debug("Printing map entries to the user...");
        for(Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println("[" + entry.getKey() + "] " + entry.getValue());
            this.logger.info("Showing entry to the user: " + entry.getKey());
        }
        this.logger.debug("Map printed to the user");
    }

    @Override
    public String getUserChoice(Map<String, String> options) {
        while(true) {
            String userInput = this.getUserInput();
            if(userInput == null) break;
            if(validateUserChoice(options, userInput)) return userInput;
            this.show("Invalid input, try again");
        }
        return null;
    }

    @Override
    public Boolean validateUserChoice(Map<String, String> options, String userChoice) {
        return options.containsKey(userChoice);
    }

    @Override
    public void displayErrorMessage(String message) {
        System.out.println(message);
        this.logger.info("Printing error message to the user: " + message);
    }

}
