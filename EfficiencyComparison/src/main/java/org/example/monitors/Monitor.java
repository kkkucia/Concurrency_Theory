package org.example.monitors;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


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


    public Monitor(int max) {
        bufferValue = 0;
        maxBufferValue = max;
        lock = new ReentrantLock();
        firstProducerCondition = lock.newCondition();
        firstConsumerCondition = lock.newCondition();
        restProducersCondition = lock.newCondition();
        restConsumersCondition = lock.newCondition();
        isFirstProducerWaiting = false;
        isFirstConsumerWaiting = false;
    }


    public void produce(int toProduce) {
        try {
            lock.lock();

            while (isFirstProducerWaiting) {
                restProducersCondition.await();
            }

            while (bufferValue + toProduce > maxBufferValue) {
                isFirstProducerWaiting = true;
                firstProducerCondition.await();
            }
            bufferValue += toProduce;
            isFirstProducerWaiting = false;
            restProducersCondition.signal();
            firstConsumerCondition.signal();

        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            lock.unlock();
        }
    }

    public void consume(int toConsume) {
        try {
            lock.lock();

            while (isFirstConsumerWaiting) {
                restConsumersCondition.await();
            }

            while (bufferValue < toConsume) {
                this.isFirstConsumerWaiting = true;
                this.firstConsumerCondition.await();
            }
            bufferValue -= toConsume;
            isFirstConsumerWaiting = false;
            restConsumersCondition.signal();
            firstProducerCondition.signal();

        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            lock.unlock();
        }
    }
}

