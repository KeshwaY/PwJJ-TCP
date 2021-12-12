package me.damianciepiela.client;

import java.util.Map;

public record Question(String description, Map<String, String> answers) {

    @Override
    public String description() {
        return description;
    }

    @Override
    public Map<String, String> answers() {
        return answers;
    }
}

