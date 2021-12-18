package me.damianciepiela.client;

import java.io.IOException;

public record ServerTimer(ClientModel model,
                          me.damianciepiela.client.ServerTimer.Observer observer) implements Runnable {

    public interface Observer {
        void forceNextQuestion(String serverFlag);
    }

    @Override
    public void run() {
        try {
            String serverFlag = this.model.getFromServer();
            if (serverFlag != null) this.observer.forceNextQuestion(serverFlag);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
