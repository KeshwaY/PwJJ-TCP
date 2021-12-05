package me.damianciepiela;

import me.damianciepiela.client.Client;
import me.damianciepiela.server.Server;

import java.io.IOException;

public class Main {

    static void client(String address, int port) {
        LoggerAdapter loggerAdapter = createLogger(Client.class);
        try {
            Client client = new Client(address, port, loggerAdapter);
            client.test();
            shutDownHook(client, loggerAdapter);
        } catch (IOException e) {
            loggerAdapter.error(e);
        }
    }

     static void server(int port, int capacity) {
         LoggerAdapter loggerAdapter = createLogger(Server.class);
         try {
             ThreadManager threadManager = new ThreadManager(capacity);
             Server server = new Server(port, loggerAdapter, threadManager);
             server.loadQuestions("Pytania.txt");
             server.start();
             shutDownHook(server, loggerAdapter);
        } catch (IOException e) {
            loggerAdapter.error(e);
         }
     }

     static LoggerAdapter createLogger(Class<?> clazz) {
        return new LoggerAdapter(clazz);
     }

     static void shutDownHook(Closable closable, LoggerAdapter logger) {
         Runtime.getRuntime().addShutdownHook(new Thread(() -> {
             try {
                 logger.info(logger.getName() + "is shutting down...");
                 closable.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }));
     }

    public static void main(String[] args) {
        if (args.length == 0) return;

        if(args[0].equals("Client")) {
            client("localhost", 887);
        }

        if(args[0].equals("Server")) {
            int capacity = Integer.parseInt(args[1]);
            server(887, capacity);
        }
    }

}
