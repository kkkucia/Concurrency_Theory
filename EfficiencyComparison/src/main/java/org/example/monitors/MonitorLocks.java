package org.example.monitors;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class MonitorLocks implements IMonitor {
    private int bufferValue;
    private final int maxBufferValue;
    private final ReentrantLock insideLock;
    private final ReentrantLock produceLock;
    private final ReentrantLock consumeLock;
    private final Condition insideCondition;


    public MonitorLocks(int max) {
        bufferValue = 0;
        maxBufferValue = max;
        insideLock = new ReentrantLock();
        produceLock = new ReentrantLock();
        consumeLock = new ReentrantLock();
        insideCondition = insideLock.newCondition();
    }

    public void produce(int toProduce) {
        produceLock.lock();
        try {
            insideLock.lock();

            while (bufferValue + toProduce > maxBufferValue) {
                insideCondition.await();
            }
            bufferValue += toProduce;
            insideCondition.signal();
            insideLock.unlock();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            produceLock.unlock();
        }
    }

    public void consume(int toConsume) {
        consumeLock.lock();
        try {
            insideLock.lock();

            while (bufferValue < toConsume) {
                insideCondition.await();
            }
            bufferValue -= toConsume;
            insideCondition.signal();
            insideLock.unlock();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            consumeLock.unlock();
        }
    }
}

