package org.example;

public class Main {
    private static final int PRODUCERS_NUM = 1000;
    private static final int CONSUMERS_NUM = 1000;


    public static void main(String[] args) {
        Monitor monitor = new Monitor();

        for (int j = 0; j < 10; j++) {
            Thread producer = new Thread(() -> {
                for (int i = 0; i < PRODUCERS_NUM; i++) {
                    monitor.producer();
                }
            });
            producer.start();

            Thread consumer = new Thread(() -> {
                for (int k = 0; k < CONSUMERS_NUM; k++) {
                    monitor.consumer();
                }
            });

            consumer.start();

//            producer.join();
//            consumer.join();
        }
    }
}