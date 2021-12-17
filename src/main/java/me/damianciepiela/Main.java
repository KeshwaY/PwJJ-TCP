package me.damianciepiela;

import me.damianciepiela.client.ClientController;
import me.damianciepiela.client.ClientModel;
import me.damianciepiela.client.ClientView;
import me.damianciepiela.server.*;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class Main {

    static void client(String address, int port) {
        LoggerAdapter clientControllerlogger = createLogger(ClientController.class);
        LoggerAdapter clientModelLogger = createLogger(ClientModel.class);
        LoggerAdapter clientViewLogger = createLogger(ClientView.class);
        try {
            ClientModel model = new ClientModel(clientModelLogger, new Socket(address, port));
            ClientView view = new ClientView(clientViewLogger);
            ClientController client = new ClientController(model, view, clientControllerlogger);
            client.start();
            shutDownHook(client, clientControllerlogger);
        } catch (IOException e) {
            clientControllerlogger.error(e);
        }
    }

     static void server(int port, int capacity, String databaseHost, String username, String password) {
         LoggerAdapter loggerAdapter = createLogger(Server.class);
         try {
             DatabaseConnection mysqlConnection = new DatabaseConnection("jdbc:" + databaseHost, username, password);
             ThreadManager threadManager = new ThreadManager(createLogger(ThreadManager.class), capacity);
             Server server = new Server(port, loggerAdapter, threadManager, mysqlConnection);
             server.loadQuestions(mysqlConnection);
             server.start();
             shutDownHook(server, loggerAdapter);
         } catch (IOException | SQLException e) {
             loggerAdapter.error(e);
         }
     }

     static LoggerAdapter createLogger(Class<?> clazz) {
        return new LoggerAdapter(clazz);
     }

     static void shutDownHook(Closable closable, LoggerAdapter logger) {
         Runtime.getRuntime().addShutdownHook(new Thread(() -> {
             try {
                 logger.info(logger.getName() + " is shutting down...");
                 closable.close();
             } catch (IOException | SQLException e) {
                 e.printStackTrace();
             }
         }));
     }



    public static void main(String[] args) {
        if (args.length == 0) return;

        // TODO: change static values to args interpretation
        if(args[0].equals("Client")) {
            String serverHost = args[1];
            int serverPort = Integer.parseInt(args[2]);
            client(serverHost, serverPort);
        }

        if(args[0].equals("Server")) {
            int serverPort = Integer.parseInt(args[1]);
            int capacity = Integer.parseInt(args[2]);
            String databaseHost = args[3];
            String username = args[4];
            String password = args[5];
            server(serverPort, capacity, databaseHost, username, password);
        }
    }

}
