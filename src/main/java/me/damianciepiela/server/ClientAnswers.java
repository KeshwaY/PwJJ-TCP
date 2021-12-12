package me.damianciepiela.server;

import java.util.List;

public record ClientAnswers(String clientId, int score, List<String> answers) {

    @Override
    public String clientId() {
        return clientId;
    }

    @Override
    public int score() {
        return score;
    }

    @Override
    public List<String> answers() {
        return answers;
    }

}
