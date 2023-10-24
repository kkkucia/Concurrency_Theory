package org.example;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {
    private int bufferValue;
    private final int maxBufferValue;
    private final Lock lock;
    private final Condition consumersCond;
    private final Condition producersCond;

    public Monitor(int max) {
        bufferValue = 0;
        maxBufferValue = max * 2;
        lock = new ReentrantLock();
        consumersCond = lock.newCondition();
        producersCond = lock.newCondition();
    }

    public void produce(int toProduce) {
        lock.lock();
        System.out.println("Producer: " + Thread.currentThread().getName() + " ; Chce produkować " + toProduce + " ; Buffor " + bufferValue);
        try {
            while (bufferValue + toProduce > maxBufferValue) {
                producersCond.await();
            }
            bufferValue += toProduce;
            System.out.println("Producer: " + Thread.currentThread().getName() + " ; Produkuje " + toProduce + " ; Buffor " + bufferValue);
            consumersCond.signal();

        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            lock.unlock();
        }
    }

    public void consume(int toConsume) {
        lock.lock();
        System.out.println("Consumer: " + Thread.currentThread().getName() + " ; Chce konsumować " + toConsume + " ; Buffor " + bufferValue);
        try {
            while (bufferValue - toConsume < 0) {
                consumersCond.await();
            }
            bufferValue -= toConsume;
            System.out.println("Consumer: " + Thread.currentThread().getName() + " ; Konsumuje  " + toConsume + " ; Buffor " + bufferValue);
            producersCond.signal();

        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            lock.unlock();
        }
    }
}


