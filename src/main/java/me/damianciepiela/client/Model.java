package me.damianciepiela.client;

import me.damianciepiela.Connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Model implements Connection {

    protected String getFrom(DataInputStream dataInputStream) throws IOException {
        return Connection.getFromSource(dataInputStream);
    }

    protected void sendTo(DataOutputStream dataOutputStream, String message) throws IOException {
        Connection.sendToSource(dataOutputStream, message);
    }

    protected boolean checkConnection(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException{
        return Connection.refreshConnection(dataOutputStream, dataInputStream);
    }

}
