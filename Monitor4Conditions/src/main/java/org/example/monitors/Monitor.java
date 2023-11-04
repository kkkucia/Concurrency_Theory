package org.example.monitors;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static org.example.tools.Colors.*;

public class Monitor implements IMonitor {
    private int bufferValue;
    private final int maxBufferValue;
    private final ReentrantLock lock;
    private final Condition firstProducerCondition;
    private final Condition firstConsumerCondition;
    private final Condition restProducersCondition;
    private final Condition restConsumersCondition;
    private boolean isFirstProducerWaiting;
    private boolean isFirstConsumerWaiting;

    private final List<String> firstConsumerList;
    private final List<String> firstProducerList;
    private final List<String> bufferConsumerList;
    private final List<String> bufferProducerList;


    public Monitor(int max) {
        bufferValue = 0;
        maxBufferValue = max * 2;
        lock = new ReentrantLock(true);
        firstProducerCondition = lock.newCondition();
        firstConsumerCondition = lock.newCondition();
        restProducersCondition = lock.newCondition();
        restConsumersCondition = lock.newCondition();
        isFirstProducerWaiting = false;
        isFirstConsumerWaiting = false;

        firstConsumerList = new ArrayList<>();
        firstProducerList = new ArrayList<>();
        bufferConsumerList = new ArrayList<>();
        bufferProducerList = new ArrayList<>();
    }

    public void produce(int toProduce, String producerName) {
        String info = producerName + "(" + toProduce + ")";
        try {
            lock.lock();
            firstProducerList.add(producerName);
            printProducerWantProduce(toProduce, producerName);

            while (isFirstProducerWaiting) {
                restProducersCondition.await();
            }

            firstProducerList.remove(producerName);
            bufferProducerList.add(info);

            while (bufferValue + toProduce > maxBufferValue) {
                isFirstProducerWaiting = true;
                firstProducerCondition.await();
            }
            bufferValue += toProduce;
            isFirstProducerWaiting = false;
            bufferProducerList.remove(info);
            printProducerProducing(toProduce, producerName);
            restProducersCondition.signal();
            firstConsumerCondition.signal();

        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            lock.unlock();
        }
    }

    public void consume(int toConsume, String consumerName) {
        String info = consumerName + "(" + toConsume + ")";
        try {
            lock.lock();
            firstConsumerList.add(consumerName);
            printConsumerWantConsume(toConsume, consumerName);
            while (isFirstConsumerWaiting) {
                restConsumersCondition.await();
            }

            firstConsumerList.remove(consumerName);
            bufferConsumerList.add(info);

            while (bufferValue < toConsume) {
                this.isFirstConsumerWaiting = true;
                this.firstConsumerCondition.await();
            }
            bufferValue -= toConsume;
            bufferConsumerList.remove(info);
            printConsumerConsuming(toConsume, consumerName);
            isFirstConsumerWaiting = false;
            restConsumersCondition.signal();
            firstProducerCondition.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            lock.unlock();
        }
    }

    private void printProducerWantProduce(int toProduce, String producerName) {
        System.out.println("CZYNNOŚĆ  : Producer " + YELLOW + producerName + END + ": Chce produkować " + GREEN + toProduce + END + " ; Buffor " + GREEN + bufferValue + END);
        printWaiters();
    }

    private void printProducerProducing(int toProduce, String producerName) {
        System.out.println("CZYNNOŚĆ  : Producer " + YELLOW + producerName + END + ": Produkuje       " + GREEN + toProduce + END + " ; Buffor " + GREEN + bufferValue + END);
        printWaiters();
    }

    private void printConsumerWantConsume(int toConsume, String consumerName) {
        System.out.println("CZYNNOŚĆ  : Consumer " + YELLOW + consumerName + END + ": Chce konsumować " + GREEN + toConsume + END + " ; Buffor " + GREEN + bufferValue + END);
        printWaiters();
    }

    private void printConsumerConsuming(int toConsume, String consumerName) {
        System.out.println("CZYNNOŚĆ  : Consumer " + YELLOW + consumerName + END + ": Konsumuje       " + GREEN + toConsume + END + " ; Buffor " + GREEN + bufferValue + END);
        printWaiters();
    }

    private void printWaiters() {
        System.out.println(
                "OCZEKUJĄCY: " +
                        "Pierwszy producent: " + YELLOW + firstProducerList + END + " " +
                        "Zasoby  producent: " + YELLOW + bufferProducerList + END + " " +
                        "Pierwszy konsument: " + YELLOW + firstConsumerList + END + " " +
                        "Zasoby  konsument: " + YELLOW + bufferConsumerList + END + "\n");
    }
}

