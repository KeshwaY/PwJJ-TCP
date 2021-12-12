package me.damianciepiela.client;

import me.damianciepiela.Logable;
import me.damianciepiela.LoggerAdapter;

import java.util.Map;
import java.util.Scanner;

public abstract class ConsoleView implements View<String, String>, Logable {

    protected final LoggerAdapter logger;

    public ConsoleView(LoggerAdapter loggerAdapter) {
        this.logger = loggerAdapter;
    }

    @Override
    public String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
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
            if(validateUserChoice(options, userInput)) return userInput;
            this.show("Invalid input, try again");
        }
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
