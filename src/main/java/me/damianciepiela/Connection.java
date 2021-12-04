package me.damianciepiela;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class Connection {
    public static void sendTo(DataOutputStream dataOutputStream, String text) throws IOException {
        dataOutputStream.writeUTF(text);
    }

    public static String getFrom(DataInputStream dataInputStream) throws IOException {
        return dataInputStream.readUTF();
    }
}
