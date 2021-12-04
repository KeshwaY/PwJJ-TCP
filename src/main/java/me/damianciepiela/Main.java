package me.damianciepiela;

import me.damianciepiela.client.Client;
import me.damianciepiela.server.Server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if(args[0].equals("Client")) {
            try {
                LoggerAdapter loggerAdapter = new LoggerAdapter(Client.class);
                Client client = new Client("localhost", 887, loggerAdapter);
                client.test();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(args[0].equals("Server")) {
            try {
                LoggerAdapter loggerAdapter = new LoggerAdapter(Server.class);
                Server server = new Server(887, loggerAdapter);
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
