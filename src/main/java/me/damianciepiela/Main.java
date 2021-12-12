package me.damianciepiela;

import me.damianciepiela.client.ClientController;
import me.damianciepiela.client.ClientModel;
import me.damianciepiela.client.ClientView;
import me.damianciepiela.server.FileCondition;
import me.damianciepiela.server.QuestionFormattingException;
import me.damianciepiela.server.Server;
import me.damianciepiela.server.ThreadManager;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Objects;

public class Main {

    static void client(String address, int port) {
        LoggerAdapter clientControllerlogger = createLogger(ClientTest.class);
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

     static void server(int port, int capacity) {
         LoggerAdapter loggerAdapter = createLogger(Server.class);
         try {
             FileCondition answersDatabase = new FileCondition(getFile("bazaOdpowiedzi.txt"));
             FileCondition scoresDatabase = new FileCondition(getFile("wyniki.txt"));
             ThreadManager threadManager = new ThreadManager(createLogger(ThreadManager.class), capacity, answersDatabase, scoresDatabase);
             Server server = new Server(port, loggerAdapter, threadManager);
             server.loadQuestions("Pytania.txt");
             server.start();
             shutDownHook(server, loggerAdapter);
        } catch (IOException | QuestionFormattingException | URISyntaxException e) {
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
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }));
     }

     static File getFile(String fileName) throws URISyntaxException {
         ClassLoader classloader = Thread.currentThread().getContextClassLoader();
         URI uri = Objects.requireNonNull(classloader.getResource(fileName)).toURI();
         return Paths.get(uri).toFile();
     }

    public static void main(String[] args) {
        if (args.length == 0) return;

        // TODO: change static values to args interpretation
        if(args[0].equals("Client")) {
            client("localhost", 887);
        }

        if(args[0].equals("Server")) {
            int capacity = Integer.parseInt(args[1]);
            server(887, capacity);
        }
    }

}
