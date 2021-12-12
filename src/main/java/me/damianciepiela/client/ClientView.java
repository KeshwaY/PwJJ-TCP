package me.damianciepiela.client;

import me.damianciepiela.LoggerAdapter;

public final class ClientView extends ConsoleView {

    public ClientView(LoggerAdapter loggerAdapter) {
        super(loggerAdapter);
    }

    @Override
    public void show() {
        show("Waiting for questions...");
    }

    public Identity getIdentityFromUser() {
        boolean validIdFormat = false;

        String id;
        String name;
        String surname;

        do {
            show("Podaj numer albumu: ");
            id = getUserInput();
            show("Podaj sowje imie: ");
            name = getUserInput();
            show("Podaj swoje nazwisko: ");
            surname = getUserInput();

            if (stringContainsOnlyNumbers(id) && stringContainsOnlyLetters(name) && stringContainsOnlyLetters(surname)) {
                validIdFormat = true;
            } else {
                displayErrorMessage("Your input is invalid.");
            }

        } while(!validIdFormat);

        return new Identity(id, name, surname);
    }

    private boolean stringContainsOnlyNumbers(String text) {
        return text.matches("[0-9]+");
    }

    private boolean stringContainsOnlyLetters(String text) {
        return text.matches("[a-zA-Z]+");
    }

    public String showQuestionAndGetAnswer(Question question) {
        show(question.description());
        show(question.answers());
        return getUserChoice(question.answers());
    }

}
