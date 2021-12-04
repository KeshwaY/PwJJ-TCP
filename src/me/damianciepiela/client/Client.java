package me.damianciepiela.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private Socket socket;

    public Client(String address, int port) throws IOException {
        this.socket = new Socket(address, port);
    }

    public void test() throws IOException {
        DataOutputStream outToServer = new DataOutputStream(this.socket.getOutputStream());
        outToServer.writeUTF("ASD");
        outToServer.flush();
        DataInputStream inFromServer = new DataInputStream(this.socket.getInputStream());
        System.out.println(inFromServer.readUTF());
        outToServer.close();
        inFromServer.close();
    }
}
