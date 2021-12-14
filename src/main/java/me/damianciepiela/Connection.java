package me.damianciepiela;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public interface Connection {

    static void sendClient(DatagramSocket socket, DatagramPacket receivePacket, DatagramPacket sendPacket, String text) throws IOException {

        sendPacket.setData(text.getBytes());

        while (true) {
            socket.send(sendPacket);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            if (response.equals("FLAG_RECEIVED")) break;
        }

    }

    static String  getClient(DatagramSocket socket, DatagramPacket receivePacket, DatagramPacket sendPacket) throws IOException {
        String text = "";
        while(true) {
           socket.receive(receivePacket);
           text = new String(receivePacket.getData(), 0, receivePacket.getLength());
           if(!text.equals("FLAG_PASS")) break;
        }
        return text;
    }

    static void sendServer(DatagramSocket socket, DatagramPacket receivePacket, DatagramPacket sendPacket, String text) throws IOException {
        sendPacket.setData(text.getBytes());
        socket.send(sendPacket);
    }

    static String getServer(DatagramSocket socket, DatagramPacket receivePacket, DatagramPacket sendPacket) throws IOException {
        socket.receive(receivePacket);
        String text = new String(receivePacket.getData(), 0, receivePacket.getLength());
        sendPacket.setData("FLAG_RECEIVED".getBytes());
        socket.send(sendPacket);
        return text;
    }

}

/*
static void sendToSource(DatagramSocket socket, DatagramPacket receivePacket, DatagramPacket sendPacket, String text) throws IOException {

        sendPacket.setData(text.getBytes());

        boolean valid = true;

        while (valid) {
            System.out.println(text);
            socket.send(sendPacket);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println(response);
            if (text.equals(response)) valid = false;
        }
    }

    static String getFromSource(DatagramSocket socket, DatagramPacket receivePacket, DatagramPacket sendPacket) throws IOException, ClassNotFoundException {

        String text = "";

        boolean valid = true;

        while (valid) {
            System.out.println("test");
            socket.receive(receivePacket);
            System.out.println("test2");
            text = new String(receivePacket.getData(), 0, receivePacket.getLength());
            if(!text.equals("Pass")) {
                sendPacket.setData(text.getBytes());
                socket.send(sendPacket);
                valid = false;
            }
            sendPacket.setData("Pass".getBytes());
            socket.send(sendPacket);
        }
        return text;
    }
 */