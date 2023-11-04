package org.example.threads;

import org.example.tools.RandomNumber;
import org.example.monitors.IMonitor;

public class Producer extends Thread {

    private final IMonitor monitor;
    private final RandomNumber randomNumber;
    private final String name;

    public Producer(IMonitor monitor, RandomNumber randomNumber, String name) {
        this.monitor = monitor;
        this.randomNumber = randomNumber;
        this.name = name;
    }

    @Override
    public void run() {
        while (true) {
            int toProduce = randomNumber.get();
            monitor.produce(toProduce, name);
        }
    }
}
