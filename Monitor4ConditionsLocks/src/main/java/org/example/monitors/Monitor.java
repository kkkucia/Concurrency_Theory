package org.example.monitors;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static org.example.tools.Colors.*;

public class Monitor implements IMonitor {
    private int bufferValue;
    private final int maxBufferValue;
    private final ReentrantLock insideLock;
    private final ReentrantLock produceLock;
    private final ReentrantLock consumeLock;
    private final Condition insideCondition;


    public Monitor(int max) {
        bufferValue = 0;
        maxBufferValue = max * 2;
        insideLock = new ReentrantLock();
        produceLock = new ReentrantLock();
        consumeLock = new ReentrantLock();
        insideCondition = insideLock.newCondition();
    }

    public void producet(int toProduce, String producerName) {
        try {
            produceLock.lock();
            insideLock.lock();

            printProducerWantProduce(toProduce, producerName);

            while (bufferValue + toProduce > maxBufferValue) {
                insideCondition.await();
            }
            bufferValue += toProduce;
            printProducerProducing(toProduce, producerName);
            insideCondition.signal();

        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            insideLock.unlock();
            produceLock.lock();
        }
    }

    public void consumet(int toConsume, String consumerName) {
        try {
            consumeLock.lock();
            insideLock.lock();

            printConsumerWantConsume(toConsume, consumerName);

            while (bufferValue < toConsume) {
                insideCondition.await();
            }
            bufferValue -= toConsume;
            printConsumerConsuming(toConsume, consumerName);
            insideCondition.signal();

        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            insideLock.unlock();
            consumeLock.unlock();
        }
    }

    private void printProducerWantProduce(int toProduce, String producerName) {
        System.out.println("Producer " + YELLOW + producerName + END + ": Chce produkować " + GREEN + toProduce + END + " ; Buffor " + GREEN + bufferValue + END);
    }

    private void printProducerProducing(int toProduce, String producerName) {
        System.out.println("Producer " + YELLOW + producerName + END + ": Produkuje       " + GREEN + toProduce + END + " ; Buffor " + GREEN + bufferValue + END);
    }

    private void printConsumerWantConsume(int toConsume, String consumerName) {
        System.out.println("Consumer " + YELLOW + consumerName + END + ": Chce konsumować " + GREEN + toConsume + END + " ; Buffor " + GREEN + bufferValue + END);
    }

    private void printConsumerConsuming(int toConsume, String consumerName) {
        System.out.println("Consumer " + YELLOW + consumerName + END + ": Konsumuje       " + GREEN + toConsume + END + " ; Buffor " + GREEN + bufferValue + END);
    }

    public void produce(int toProduce, String producerName) {
        long startTime = System.nanoTime(); // Początkowy czas wykonania
        try {
            produceLock.lock();
            insideLock.lock();

            printProducerWantProduce(toProduce, producerName);

            while (bufferValue + toProduce > maxBufferValue) {
                insideCondition.await();
            }
            bufferValue += toProduce;
            printProducerProducing(toProduce, producerName);
            insideCondition.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            insideLock.unlock();
            produceLock.unlock();
        }
        long endTime = System.nanoTime(); // Końcowy czas wykonania
        long executionTime = endTime - startTime;
        System.out.println("Czas produkcji dla "+ YELLOW + producerName + END + ": " + executionTime + " nanosekundy");
    }

    public void consume(int toConsume, String consumerName) {
        long startTime = System.nanoTime(); // Początkowy czas wykonania
        try {
            consumeLock.lock();
            insideLock.lock();

            printConsumerWantConsume(toConsume, consumerName);

            while (bufferValue < toConsume) {
                insideCondition.await();
            }
            bufferValue -= toConsume;
            printConsumerConsuming(toConsume, consumerName);
            insideCondition.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            insideLock.unlock();
            consumeLock.unlock();
        }
        long endTime = System.nanoTime(); // Końcowy czas wykonania
        long executionTime = endTime - startTime;
        System.out.println("Czas konsumpcji dla " + YELLOW + consumerName + END + ": " + executionTime + " nanosekundy");
    }
}

