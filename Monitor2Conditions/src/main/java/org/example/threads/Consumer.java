package org.example.threads;


import org.example.Monitor;
import org.example.RandomNumber;

public class Consumer extends Thread {
    private final Monitor monitor;
    private final RandomNumber randomNumber;

    public Consumer(Monitor monitor, RandomNumber randomNumber) {
        this.monitor = monitor;
        this.randomNumber = randomNumber;
    }

    @Override
    public void run() {
        while (true) {
            int toConsume = randomNumber.get();
            monitor.consume(toConsume);
        }
    }
}
