package me.damianciepiela;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Connection {
    static void sendToSource(DataOutputStream dataOutputStream, String text) throws IOException {
        dataOutputStream.writeUTF(text);
    }

    static String getFromSource(DataInputStream dataInputStream) throws IOException {
        return dataInputStream.readUTF();
    }
}
