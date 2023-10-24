package org.example.threads;

import org.example.Monitor;
import org.example.RandomNumber;

public class Producer extends Thread {
    private final Monitor monitor;
    private final RandomNumber randomNumber;

    public Producer(Monitor monitor, RandomNumber randomNumber) {
        this.monitor = monitor;
        this.randomNumber = randomNumber;
    }

    @Override
    public void run() {
        while (true) {
            int toProduce = randomNumber.get();
            monitor.produce(toProduce);
        }
    }
}
