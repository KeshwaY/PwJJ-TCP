package me.damianciepiela.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

public class ReadUserInputTask implements Callable<String> {
    @Override
    public String call() throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String input;
        do {
            input = bufferedReader.readLine();
        } while ("".equals(input));
        return input;
    }
}
