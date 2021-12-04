package me.damianciepiela;

import me.damianciepiela.client.Client;
import me.damianciepiela.server.Server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) return;
        if(args[0].equals("Client")) {
            LoggerAdapter loggerAdapter = new LoggerAdapter(Client.class);
            try {
                Client client = new Client("localhost", 887, loggerAdapter);
                client.test();
            } catch (IOException e) {
                loggerAdapter.error(e);
            }
        }
        if(args[0].equals("Server")) {
            LoggerAdapter loggerAdapter = new LoggerAdapter(Server.class);
            try {
                Server server = new Server(887, loggerAdapter);
                server.start();
            } catch (IOException e) {
                loggerAdapter.error(e);
            }
        }
    }

}
