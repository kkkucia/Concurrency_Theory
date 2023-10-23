package org.example;


import java.util.LinkedList;

public class Monitor {
    private final LinkedList<Integer> buffer;

    public Monitor() {
        this.buffer = new LinkedList<Integer>();
    }

    public synchronized void producer(){
        try {
            while (!buffer.isEmpty()){
                wait();
            }
            buffer.push(1);
            System.out.println("Producer: " + buffer);
            notifyAll();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void consumer(){
        try {
            while (buffer.isEmpty()){
                wait();
            }
            buffer.pop();
            System.out.println("Consumer: " + buffer);
            notifyAll();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


// B=0
// 1 producent B = 1
// 2 producent wchodzi do synchronized i czeka B = 1
// 1 producent wchodzi do synchronized i czeka B = 1
// Konsumer konsumuje, notify i wychodzi B = 0
// 1 producent dostaje notify, próbuje uzyskac dostep, watek zmienia stan wiszenia na wait i czeka na swoj czas (stan czekajacy na uruchomienie)
// konsumer wchodzi do synchronized i czeka na B = 0 (zostało przyznane konsumentowi)
// Na wait mamy konsumer i 1 producer => ZAKLESZCZENIE
