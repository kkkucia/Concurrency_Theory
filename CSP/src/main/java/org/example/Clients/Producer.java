package org.example.Clients;

import org.example.Buffer.DoubleChannel;
import org.example.Packet.Packet;
import org.example.Packet.PacketStatus;
import org.example.Packet.PacketType;

public class Producer implements Client {
    private final DoubleChannel bufferCommunication;
    private final int toProduce;
    private final int index;
    private int successCounter;
    private int failureCounter;

    public Producer(DoubleChannel bufferCommunication, int index, int toProduce ) {
        this.bufferCommunication = bufferCommunication;
        this.toProduce = toProduce;
        this.index = index;
        this.successCounter = 0;
        this.failureCounter = 0;
    }

    public void run() {
        while(true) {
            Packet packetRequest = new Packet(PacketType.PRODUCTION_REQUEST, toProduce, index, bufferCommunication.getInformationChannel());

            System.out.println("Producer " + index + " request: " + packetRequest);

            bufferCommunication.writeToDelivery(packetRequest);

            Packet packetResponse = bufferCommunication.readFromInformation();

            System.out.println("Producer " + index + " response: " + packetResponse);

            checkPacketResponseStatus(packetResponse);
        }
    }

    private void checkPacketResponseStatus(Packet packetResponse) {
        if(packetResponse.getStatus() == PacketStatus.SUCCESS) {
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
        System.out.println("Producer " + index + "; Balance [SUCCESS : FAILURE] [ " + successCounter + " : " + failureCounter + " ]");
    }

//    private void magicChange(){
//        Packet packetRequest = new Packet(PacketType.CONSUMPTION_REQUEST, (toProduce * (-1)), index, bufferCommunication.getInformationChannel());
//
//        System.out.println("Consumer " + index + " request: " + packetRequest);
//        bufferCommunication.writeToDelivery(packetRequest);
//        Packet packetResponse = bufferCommunication.readFromInformation();
//        System.out.println("Consumer " + index + " response: " + packetResponse);
//        checkPacketResponseStatus(packetResponse);
//    }
}