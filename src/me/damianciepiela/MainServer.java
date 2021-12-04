package me.damianciepiela;

import me.damianciepiela.server.Server;

import java.io.IOException;

public class MainServer {

    public static void main(String[] args) {
        try {
            Server server = new Server(887);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
