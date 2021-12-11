package me.damianciepiela.server;

import me.damianciepiela.FileCondition;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FutureClient extends FutureTask<ClientAnswers> {

    private final FileCondition answersDatabase;
    private final FileCondition scoresDatabase;

    public FutureClient(ServerClient callable, FileCondition answersDatabase, FileCondition scoresDatabase) {
        super(callable);
        this.answersDatabase = answersDatabase;
        this.scoresDatabase = scoresDatabase;
    }

    @Override
    public void done() {
        try {
            ClientAnswers finalScore = this.get();
            this.scoresDatabase.writeToFile(finalScore.getClientId() + ": " + finalScore.getScore());
            StringBuilder stringBuilder = new StringBuilder(finalScore.getClientId() + ":\n");
            for(String answer : finalScore.getAnswers()) {
                stringBuilder.append(" ").append(answer).append("\n");
            }
            this.answersDatabase.writeToFile(stringBuilder.toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
