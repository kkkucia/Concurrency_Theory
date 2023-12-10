package org.example.monitors;


public interface IMonitor {
    void produce(int toProduce);

    void consume(int toConsume);
}