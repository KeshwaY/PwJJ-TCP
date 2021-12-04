package me.damianciepiela.server;

import java.net.Socket;

public class Client {

    private final Socket socket;
    private final String name;
    private final String surrname;
    private final String id;

    public Client(Socket socket, String name, String surrname, String id) {
        this.socket = socket;
        this.name = name;
        this.surrname = surrname;
        this.id = id;
    }

}
