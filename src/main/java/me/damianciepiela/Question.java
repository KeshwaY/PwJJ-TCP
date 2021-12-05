package me.damianciepiela;

import java.util.Map;

public class Question {
    private final String description;
    private final String correctAnswer;
    private final Map<String, String> answers;

    public Question(String description, String correctAnswear, Map<String, String> allAnswers) {
        this.description = description;
        this.correctAnswer = correctAnswear;
        this.answers = allAnswers;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, String> getAnswers() {
        return answers;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

}
