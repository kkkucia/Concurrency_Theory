package org.example.Clients;

import org.example.Buffer.DoubleChannel;
import org.example.Packet.Packet;
import org.example.Packet.PacketStatus;
import org.example.Packet.PacketType;

public class Consumer implements Client {
    private final DoubleChannel bufferCommunication;
    private final int toConsume;
    private final int index;
    private int successCounter;
    private int failureCounter;

    public Consumer(DoubleChannel bufferCommunication, int index, int toConsume) {
        this.bufferCommunication = bufferCommunication;
        this.toConsume = (-1) * toConsume;
        this.index = index;
        this.successCounter = 0;
        this.failureCounter = 0;
    }


    public void run() {
        while (true) {
            Packet packetRequest = new Packet(PacketType.CONSUMPTION_REQUEST, toConsume, index, bufferCommunication.getInformationChannel());

            System.out.println("Consumer " + index + " request: " + packetRequest);

            bufferCommunication.writeToDelivery(packetRequest);

            Packet packetResponse = bufferCommunication.readFromInformation();

            System.out.println("Consumer " + index + " response: " + packetResponse);

            checkPacketResponseStatus(packetResponse);
        }
    }

    private void checkPacketResponseStatus(Packet packetResponse) {
        if (packetResponse.getStatus() == PacketStatus.SUCCESS) {
            this.successCounter++;
        } else {
            this.failureCounter++;
//            magicChange();
        }
        showStatusBalance();
    }

    public DoubleChannel getBufferCommunication() {
        return bufferCommunication;
    }

    public void showStatusBalance() {
        System.out.println("Consumer " + index + "; Balance [SUCCESS : FAILURE] [ " + successCounter + " : " + failureCounter + " ]");
    }

//    private void magicChange(){
//        Packet packetRequest = new Packet(PacketType.CONSUMPTION_REQUEST, (toConsume * (-1)), index, bufferCommunication.getInformationChannel());
//
//            System.out.println("Consumer " + index + " request: " + packetRequest);
//        bufferCommunication.writeToDelivery(packetRequest);
//        Packet packetResponse = bufferCommunication.readFromInformation();
//        System.out.println("Consumer " + index + " response: " + packetResponse);
//        checkPacketResponseStatus(packetResponse);
//    }
}
