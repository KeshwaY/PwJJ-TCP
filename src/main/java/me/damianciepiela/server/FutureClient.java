package me.damianciepiela.server;

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
            this.scoresDatabase.writeToFile(finalScore.clientId() + ": " + finalScore.score());
            StringBuilder stringBuilder = new StringBuilder(finalScore.clientId() + ":\n");
            for(String answer : finalScore.answers()) {
                stringBuilder.append(" ").append(answer).append("\n");
            }
            this.answersDatabase.writeToFile(stringBuilder.toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
