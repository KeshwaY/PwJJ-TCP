package me.damianciepiela.server;

public class QuestionFormattingException extends Exception {

    public QuestionFormattingException(String message) {
        super(message);
    }

    public QuestionFormattingException(String message, String question) {
        super(message + ": " + question);
    }


}
