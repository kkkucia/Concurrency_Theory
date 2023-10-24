package org.example.threads;

import org.example.Monitor;

public class BigProducer extends Thread {
    private final Monitor monitor;
    private final int bigProduction;

    public BigProducer(Monitor monitor, int bigProduction) {
        this.monitor = monitor;
        this.bigProduction = bigProduction;
    }

    @Override
    public void run() {
        while (true) {
            monitor.produce(bigProduction);
        }
    }
}
