package me.damianciepiela.client;

public record Identity(String id, String name, String surname) {
    @Override
    public String id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String surname() {
        return surname;
    }
}
