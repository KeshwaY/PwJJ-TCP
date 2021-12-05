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

    static boolean checkIfSourceIsActive(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException {
        sendToSource(dataOutputStream, "ping");
        String fromSource = getFromSource(dataInputStream);
        return fromSource.equals("pong");
    }

    static boolean refreshConnection(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException {
        String fromSource = getFromSource(dataInputStream);
        if (fromSource.equals("ping")) {
            sendToSource(dataOutputStream, "pong");
            return true;
        }
        return false;
    }
}
