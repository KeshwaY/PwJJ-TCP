package me.damianciepiela.server;

import java.util.List;

public record Question(String ID, String description, String correctAnswer, List<Answer> answers) {

    @Override
    public String ID() {
        return ID;
    }

    @Override
    public String description() {
        return description;
    }

    public String correctAnswer() {
        return correctAnswer;
    }

    @Override
    public List<Answer> answers() {
        return answers;
    }

}
