package me.damianciepiela.server;

import java.util.Map;

public record Question(String description, String correctAnswer, Map<String, String> answers) {

    @Override
    public String description() {
        return description;
    }

    public String correctAnswer() {
        return correctAnswer;
    }

    @Override
    public Map<String, String> answers() {
        return answers;
    }

}
