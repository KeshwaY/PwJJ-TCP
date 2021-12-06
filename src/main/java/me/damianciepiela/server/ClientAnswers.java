package me.damianciepiela.server;

import java.util.List;

public record ClientAnswers(String clientId, int score, List<String> answers) {

    public String getClientId() {
        return clientId;
    }

    public int getScore() {
        return score;
    }

    public List<String> getAnswers() {
        return answers;
    }
}
