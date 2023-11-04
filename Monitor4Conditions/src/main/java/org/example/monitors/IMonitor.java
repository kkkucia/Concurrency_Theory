package org.example.monitors;


public interface IMonitor {
    void produce(int toProduce, String producerName);

    void consume(int toConsume, String consumerName);
}