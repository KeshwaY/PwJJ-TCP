package me.damianciepiela.server;

public record Answer(String ID, String charRepresentation, String description) {

    @Override
    public String ID() {
        return ID;
    }

    public String charRepresentation() {
        return charRepresentation;
    }

    @Override
    public String description() {
        return description;
    }
}
