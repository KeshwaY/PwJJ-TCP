package me.damianciepiela.client;

import me.damianciepiela.Connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public abstract class Model {

    protected String getFrom(DatagramSocket socket, DatagramPacket receivePacket, DatagramPacket sendPacket) throws IOException, ClassNotFoundException {
        return Connection.getClient(socket, receivePacket, sendPacket);
    }

    protected void sendTo(DatagramSocket socket, DatagramPacket receivePacket, DatagramPacket sendPacket, String text) throws IOException, ClassNotFoundException {
        Connection.sendClient(socket, receivePacket, sendPacket, text);
    }

}
