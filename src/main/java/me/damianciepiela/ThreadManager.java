package me.damianciepiela;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {

    private ExecutorService executorService;
    private int capacity;
    private int currentSize = 0;

    public ThreadManager(int capacity) {
        this.executorService = Executors.newFixedThreadPool(capacity);
    }

}
