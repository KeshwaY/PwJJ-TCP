package me.damianciepiela;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public interface Connection {


    static void sendToServer(DatagramSocket socket, DatagramPacket receivePacket, DatagramPacket sendPacket, ConnectionStatus connectionStatus) throws  IOException {
        sendPacket.setData(connectionStatus.name().getBytes());

        while (true) {
            socket.send(sendPacket);

            socket.receive(receivePacket);
            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            if(ConnectionStatus.REQUEST_RECOGNIZED.name().equals(response)) break;
        }
    }

    static void sendToServer(DatagramSocket socket, DatagramPacket receivePacket, DatagramPacket sendPacket, String text) throws IOException {

        sendPacket.setData(text.getBytes());

        while (true) {
            socket.send(sendPacket);

            socket.receive(receivePacket);
            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            if(ConnectionStatus.DATA_RECEIVED.name().equals(response)) break;
        }

    }

    static String  getFromServer(DatagramSocket socket, DatagramPacket receivePacket, DatagramPacket sendPacket) throws IOException {
        socket.receive(receivePacket);
        String text = new String(receivePacket.getData(), 0, receivePacket.getLength());
        return text;
    }

    static void sendToClient(DatagramSocket socket, DatagramPacket sendPacket, String text) throws IOException {
        sendPacket.setData(text.getBytes());
        socket.send(sendPacket);
    }

}