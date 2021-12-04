package me.damianciepiela.server;

import me.damianciepiela.Connection;

import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class ServerClient {

    private final Socket socket;
    private final DataInputStream inFromClient;
    private final DataOutputStream outToClient;

    private String id;
    private String name;
    private String surrname;

    public ServerClient(Socket socket) throws IOException {
        this.socket = socket;
        this.inFromClient = new DataInputStream(socket.getInputStream());
        this.outToClient = new DataOutputStream(socket.getOutputStream());
    }

    public String getFrom() throws IOException {
        return Connection.getFrom(this.inFromClient);
    }

    public void sendTo(String text) throws IOException {
        Connection.sendTo(this.outToClient, text);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurrname(String surrname) {
        this.surrname = surrname;
    }

    public void quit() throws IOException {
        this.inFromClient.close();
        this.outToClient.close();
        this.socket.close();
    }

}
