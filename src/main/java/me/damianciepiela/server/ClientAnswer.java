package me.damianciepiela.server;

public record ClientAnswer(String questionId, String answerId) {
    @Override
    public String questionId() {
        return questionId;
    }

    @Override
    public String answerId() {
        return answerId;
    }
}
