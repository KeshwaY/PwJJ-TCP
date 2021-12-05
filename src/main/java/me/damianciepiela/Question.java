package me.damianciepiela;

import java.util.List;

public class Question {
    private final String description;
    private final String correctAnswer;
    private final List<String> answers;

    public Question(String description, String correctAnswear, List<String> allAnswers) {
        this.description = description;
        this.correctAnswer = correctAnswear;
        this.answers = allAnswers;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

}
