package org.example;

//klasa
//        int
//        ++
//        --
//        wypisanie
//
//
//        10 wątków, które napisza ze jestem

//wątek 1 wykonuje


public class Main {
    public static int loopNum = 1000;
    public static int threadNum = 100;

    public static void main(String[] args) throws InterruptedException {

        Counter counter = new Counter();

        for (int j = 0; j < threadNum; j++) {
            Thread thread1 = new Thread(() -> {
                for (int i = 0; i < loopNum; i++) {
                    counter.decrement();
                }
            });

            Thread thread2 = new Thread(() -> {
                for (int i = 0; i < loopNum; i++) {
                    counter.increment();
                }
            });
            thread2.start();
            thread1.start();

            thread1.join();
            thread2.join();
        }
        counter.printI();
    }
}
