package org.example;

import org.example.Buffer.Buffer;
import org.example.Buffer.BufferContainer;
import org.example.Buffer.DoubleChannel;
import org.example.Clients.Clients;
import org.example.Clients.Consumer;
import org.example.Clients.Producer;
import org.jcsp.lang.*;

import java.util.ArrayList;
import java.util.List;

public final class PCMain {
    private final int BUFFERS_NUMBER = 4;
    private final int CLIENTS_PAIR_NUMBER = BUFFERS_NUMBER;
    private final int BUFFER_SIZE = 10;
    private final int PAYLOAD_VALUE = 1;

    private final List<CSProcess> processes = new ArrayList<>();
    private final BufferContainer bufferContainer = new BufferContainer(new ArrayList<Buffer>());
    private Clients[] clientsList;

    public PCMain() {
        createTopology();

        CSProcess[] convertedProcesses = new CSProcess[processes.size()];
        convertedProcesses = processes.toArray(convertedProcesses);

        Parallel parallel = new Parallel(convertedProcesses);
        parallel.run();
    }

    private void createTopology() {
        createClients();
        createBufferLoop();
    }

    private void createClients() {
        clientsList = new Clients[CLIENTS_PAIR_NUMBER];

        for (int i = 0; i < CLIENTS_PAIR_NUMBER; i++) {
            DoubleChannel bufferCommunicationProducer = new DoubleChannel(Channel.one2one(), Channel.one2one());
            DoubleChannel bufferCommunicationConsumer = new DoubleChannel(Channel.one2one(), Channel.one2one());

            Producer producer = new Producer(bufferCommunicationProducer, i, PAYLOAD_VALUE);
            Consumer consumer = new Consumer(bufferCommunicationConsumer, i, PAYLOAD_VALUE);
            Clients clients = new Clients(producer, consumer);

            clientsList[i] = clients;

            processes.add(producer);
            processes.add(consumer);
        }
    }

    private void createBufferLoop() {
        for (int i = 0; i < BUFFERS_NUMBER; i++) {
            DoubleChannel forwardChannel = null;
            DoubleChannel backwardChannel = null;
            boolean permission = false;

            if (i == 0) {
                forwardChannel = new DoubleChannel(Channel.one2one(), Channel.one2one());
                backwardChannel = new DoubleChannel(Channel.one2one(), Channel.one2one());
                permission = true;

            } else if (i == BUFFERS_NUMBER - 1) {
                forwardChannel = bufferContainer.bufferList().get(0).getBackwardBuffer();
                backwardChannel = bufferContainer.bufferList().get(i - 1).getForwardBuffer();

            } else {
                forwardChannel = new DoubleChannel(Channel.one2one(), Channel.one2one());
                backwardChannel = bufferContainer.bufferList().get(i - 1).getForwardBuffer();
            }

            Buffer buffer = new Buffer(BUFFER_SIZE, i, clientsList[i], forwardChannel, backwardChannel, permission);

            bufferContainer.bufferList().add(buffer);
            processes.add(buffer);
        }
    }

}


