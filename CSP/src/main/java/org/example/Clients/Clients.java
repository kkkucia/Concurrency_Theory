package org.example.Clients;

import org.example.Buffer.DoubleChannel;

import java.util.ArrayList;
import java.util.List;

public class Clients {

    private List<Client> list = new ArrayList<>();

    private int quantity;

    public Clients(Producer producer, Consumer consumer) {
        list.add(producer);
        list.add(consumer);
        quantity = list.size();
    }

    public Clients(Producer producer, Producer producer2) {
        list.add(producer);
        list.add(producer2);
        quantity = list.size();
    }

    public int getQuantity() {
        return quantity;
    }

    public DoubleChannel getClientBufferCommunication(int index) {
        return list.get(index).getBufferCommunication();
    }
}
