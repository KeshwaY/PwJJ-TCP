package me.damianciepiela.server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        System.out.println("Server created");
    }

    public void start() throws IOException {
        System.out.println("Server starting...");
        System.out.println("Server waiting for connections...");
        while (!serverSocket.isClosed()) {
            Socket client = serverSocket.accept();
            System.out.println(client);
            DataInputStream inFromClient = new DataInputStream(client.getInputStream());
            DataOutputStream outToClient = new DataOutputStream(client.getOutputStream());
            System.out.println(inFromClient.readUTF());
            outToClient.writeUTF("Test");
            outToClient.flush();
            inFromClient.close();
            outToClient.close();
        }
    }

}
