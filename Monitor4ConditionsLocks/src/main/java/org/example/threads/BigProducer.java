package org.example.threads;

import org.example.monitors.IMonitor;

public class BigProducer extends Thread {
    private final IMonitor monitor;
    private final int bigProduction;
    private final String name;

    public BigProducer(IMonitor monitor, int bigProduction, String name) {
        this.monitor = monitor;
        this.bigProduction = bigProduction;
        this.name = name;
    }

    @Override
    public void run() {
        while (true) {
            monitor.produce(bigProduction, name);
        }
    }
}
