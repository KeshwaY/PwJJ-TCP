package me.damianciepiela.client;

import java.util.Map;

public interface View<K, V> {
    void show();
    void show(String message);

    void show(Map<K, V> map);
    String getUserInput();
    K getUserChoice(Map<K, V> options);
    Boolean validateUserChoice(Map<K, V> options, K userChoice);
    void displayErrorMessage(String message);
}
