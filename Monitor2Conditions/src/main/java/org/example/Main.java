package org.example;


import org.example.threads.BigProducer;
import org.example.threads.Consumer;
import org.example.threads.Producer;


public class Main {
    private static final int PRODUCERS_NUM = 2;
    private static final int BIG_PRODUCERS_NUM = 1;
    private static final int CONSUMERS_NUM = 1;
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 10;


    public static void main(String[] args) {
        Monitor monitor = new Monitor(MAX_VALUE);
        RandomNumber randomNumberGenerator = new RandomNumber(MIN_VALUE, MAX_VALUE);


        for (int i = 0; i < PRODUCERS_NUM; i++) {
            Producer producer = new Producer(monitor, randomNumberGenerator);
            producer.start();
        }

        for (int i = 0; i < BIG_PRODUCERS_NUM; i++) {
            BigProducer bigProducer = new BigProducer(monitor, MAX_VALUE);
            bigProducer.start();
        }

        for (int i = 0; i < CONSUMERS_NUM; i++) {
            Consumer consumer = new Consumer(monitor, randomNumberGenerator);
            consumer.start();
        }
    }
}