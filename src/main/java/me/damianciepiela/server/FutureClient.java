package me.damianciepiela.server;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FutureClient extends FutureTask<ClientAnswers> {

    DatabaseConnection databaseConnection;

    public FutureClient(ServerClient callable, DatabaseConnection databaseConnection) {
        super(callable);
        this.databaseConnection = databaseConnection;
    }

    @Override
    public void done() {
        try {
            ClientAnswers finalScore = this.get();
            if (finalScore == null) return;
            System.out.println(finalScore);
            this.databaseConnection.addStudent(finalScore.clientId(), finalScore.clientName(), finalScore.clientSurname());
            this.databaseConnection.saveStudentScore(finalScore.clientId(), String.valueOf(finalScore.score()));
            List<ClientAnswer> clientAnswerList = finalScore.answers();
            for(ClientAnswer clientAnswer : clientAnswerList) {
                this.databaseConnection.saveStudentAnswer(finalScore.clientId(), clientAnswer.questionId(), clientAnswer.answerId());
            }
        } catch (InterruptedException | ExecutionException | SQLException e) {
            e.printStackTrace();
        }
    }

}
