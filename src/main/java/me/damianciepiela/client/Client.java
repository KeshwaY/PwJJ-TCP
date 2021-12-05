package me.damianciepiela.client;

import me.damianciepiela.Closable;
import me.damianciepiela.Connection;
import me.damianciepiela.LoggerAdapter;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Connection, Closable {
    private final LoggerAdapter logger;
    private final Socket socket;

    private boolean connected;

    private final DataInputStream inFromServer;
    private final DataOutputStream outToServer;

    public Client(String address, int port, LoggerAdapter logger) throws IOException {
        this.logger = logger;
        this.socket = new Socket(address, port);
        this.inFromServer = new DataInputStream(socket.getInputStream());
        this.outToServer = new DataOutputStream(socket.getOutputStream());
        this.connected = true;
        this.logger.debug("Connection established");
        this.logger.info("Client created.");
    }

    public void setIdentity() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Podaj numer albumu: ");
        int id = scanner.nextInt();
        System.out.println("Podaj imie: ");
        String name = scanner.next();
        System.out.println("Podaj nazwisko: ");
        String surname = scanner.next();

        try {
            sendTo(String.valueOf(id));
            sendTo(name);
            sendTo(surname);
        } catch (IOException e) {
            close();
        }
    }

    public void start() {
        try {
            setIdentity();
            while(connected) {
                checkConnection();
            }
        } catch (IOException e) {
            this.logger.error(e);
        }
    }

    public String getFrom() throws IOException {
        String fromServer = Connection.getFromSource(this.inFromServer);
        this.logger.debug("Got content from Server on " + this.socket.getInetAddress() + ": " + fromServer);
        return fromServer;
    }

    public void sendTo(String text) throws IOException {
        this.logger.debug("Sending to content to Server on " + this.socket.getInetAddress() + ": " + text);
        Connection.sendToSource(this.outToServer, text);
    }

    public void checkConnection() throws IOException {
        this.connected = Connection.refreshConnection(this.outToServer, this.inFromServer);
        //this.logger.debug("Server on " + this.socket.getInetAddress() + " connection status: " + this.connected);
    }

    public void close() throws IOException {
        this.socket.close();
        this.outToServer.close();
        this.inFromServer.close();
    }
}
