package org.example.program;

import org.example.monitors.IMonitor;
import org.example.threads.Consumer;
import org.example.threads.ObservableThread;
import org.example.threads.Producer;
import org.example.threads.ThreadData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ProgramWrapper implements Program{
    private final ProgramType type;
    private final IMonitor monitor;
    private final int threadCount;
    private final Supplier<IntStream> randomStreamSupplier;
    private final long executionTimeMillis;
    private final ExecutorService executorService;
    private List<ObservableThread> programThreads;

    public ProgramWrapper(ProgramType type, IMonitor monitor, int threadCount, long executionTimeMillis,
                          Supplier<IntStream> randomStreamSupplier) {
        this.type = type;
        this.monitor = monitor;
        this.threadCount = threadCount;
        this.executionTimeMillis = executionTimeMillis;
        this.randomStreamSupplier = randomStreamSupplier;
        this.executorService = Executors.newCachedThreadPool();
    }

    @Override
    public Optional<ProgramData> getData() {
        return execute() ? Optional.of(getProgramData()): Optional.empty();
    }

    private ProgramData getProgramData() {
        return new ProgramData(type, getDataRecords());
    }

    private List<ThreadData> getDataRecords() {
        return programThreads.stream()
                .map(ObservableThread::getThreadData)
                .toList();
    }

    private boolean execute() {
        programThreads = createThreads();
        List<Thread> castedThreads = castToThreads(programThreads);
        startThreads(castedThreads);
        joinThreads(programThreads);
        return true;
    }

    private List<ObservableThread> createThreads() {
        List<ObservableThread> observableThreads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Producer producer = new Producer(monitor, randomStreamSupplier.get());
            Consumer consumer = new Consumer(monitor, randomStreamSupplier.get());
            observableThreads.add(producer);
            observableThreads.add(consumer);
        }
        return observableThreads;
    }

    private List<Thread> castToThreads(List<ObservableThread> programThreads) {
        return programThreads.stream().map(t -> (Thread) t).toList();
    }

    private void startThreads(List<Thread> programThreads) {
        programThreads.forEach(executorService::submit);
    }

    private void joinThreads(List<ObservableThread> programThreads) {
        try {
            Thread.sleep(executionTimeMillis);
        } catch (InterruptedException ignored) {}
        programThreads.forEach(t -> t.setShouldExit(true));
        executorService.shutdownNow();
    }
}
