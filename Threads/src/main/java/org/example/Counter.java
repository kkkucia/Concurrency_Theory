package org.example;

public class Counter {
    private static int i = 0;

    public Counter() {
    }

    public void increment(){
        synchronized (this){
            i++;
        }
    }

    public synchronized void decrement(){
        i--;
    }

    public void printI(){
        System.out.println(i);
    }
}
