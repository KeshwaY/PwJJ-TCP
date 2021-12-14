package me.damianciepiela.client;

import me.damianciepiela.Connection;
import me.damianciepiela.ConnectionStatus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public abstract class Model {

    protected String getFrom(DatagramSocket socket, DatagramPacket receivePacket, DatagramPacket sendPacket) throws IOException {
        return Connection.getFromServer(socket, receivePacket, sendPacket);
    }

    protected void sendTo(DatagramSocket socket, DatagramPacket receivePacket, DatagramPacket sendPacket, String text) throws IOException {
        Connection.sendToServer(socket, receivePacket, sendPacket, text);
    }

    protected void sendTo(DatagramSocket socket, DatagramPacket receivePacket, DatagramPacket sendPacket, ConnectionStatus connectionStatus) throws IOException {
        Connection.sendToServer(socket, receivePacket, sendPacket, connectionStatus);
    }


}
