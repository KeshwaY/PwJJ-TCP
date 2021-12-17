package me.damianciepiela.server;

import java.util.List;

public record ClientAnswers(String clientId, String clientName, String clientSurname, int score, List<ClientAnswer> answers) {

    @Override
    public String clientId() {
        return clientId;
    }

    @Override
    public String clientName() {
        return clientName;
    }

    public String clientSurname() {
        return clientSurname;
    }

    @Override
    public int score() {
        return score;
    }

    @Override
    public List<ClientAnswer> answers() {
        return answers;
    }

}
