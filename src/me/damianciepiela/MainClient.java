package me.damianciepiela;

import me.damianciepiela.client.Client;

import java.io.IOException;

public class MainClient {

    public static void main(String[] args) {
        try {
            Client client = new Client("localhost", 887);
            client.test();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
