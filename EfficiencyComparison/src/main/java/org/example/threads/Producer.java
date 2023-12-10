package org.example.threads;

import org.example.monitors.IMonitor;

import java.lang.management.ManagementFactory;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

import static java.lang.Math.max;


public class Producer extends Thread implements ObservableThread{

    private final IMonitor monitor;
    private final PrimitiveIterator.OfInt randomPortions;
    private long nanoTime = 0;
    private long operationCounter;
    private boolean shouldExit;
    private final ThreadType type;

    public Producer(IMonitor monitor, IntStream randomPortionStream) {
        this.monitor = monitor;
        this.randomPortions = randomPortionStream.iterator();
        this.operationCounter = 0;
        this.type = ThreadType.PRODUCER;
    }

    @Override
    public void run() {
        while (!shouldExit) {
            int toProduce = randomPortions.next();
            monitor.produce(toProduce);
            operationCounter += 1;
            nanoTime = max(nanoTime, ManagementFactory.getThreadMXBean().getThreadCpuTime(currentThread().threadId()));
        }
    }

    @Override
    public ThreadData getThreadData() {
        return new ThreadData(type, operationCounter, nanoTime);
    }

    @Override
    public void setShouldExit(boolean shouldExit) {
        this.shouldExit = shouldExit;
    }
}
