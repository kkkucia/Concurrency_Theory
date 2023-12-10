package org.example.Buffer;

import org.example.Clients.Clients;
import org.example.Packet.Packet;
import org.example.Packet.PacketStatus;
import org.example.Packet.PacketType;
import org.jcsp.lang.*;

import java.util.*;


public class Buffer implements CSProcess {
    private final int size;
    private final int bufferIndex;
    private int bufferValue;
    private final Clients clients;
    private final Queue<Packet> packetsToSend;
    private final DoubleChannel forwardBuffer;
    private final DoubleChannel backwardBuffer;
    private boolean permission;
    private int successCounter;
    private int failureCounter;

    public Buffer(int size, int bufferIndex, Clients clients, DoubleChannel forwardBuffer, DoubleChannel backwardBuffer, Boolean permission) {
        this.size = size;
        this.bufferValue = 0;
        this.clients = clients;
        this.bufferIndex = bufferIndex;
        this.forwardBuffer = forwardBuffer;
        this.backwardBuffer = backwardBuffer;
        this.packetsToSend = new LinkedList<>();
        this.permission = permission;
        this.successCounter = 0;
        this.failureCounter = 0;
    }

    @Override
    public void run() {
        Alternative alternative = prepareAlternative();

        while (true) {
            while (packetsToSend.size() == 0) {
                System.out.println("Buffer " + bufferIndex + "; Waiting for clients... ; Buff Value: " + bufferValue);

//                int index = alternative.fairSelect(); // Jeśli więcej niż jeden strażnik będzie gotowy, ustala priorytet swojego wyboru tak, aby
                                                      // strażnik, którego wybrał ostatnim razem, gdy został wezwany, miał tym razem najniższy priorytet.
                int index = alternative.select();
                if (index != -1) {
                    Packet packet = clients.getClientBufferCommunication(index).readFromDelivery();
                    packet.setCurrentBufferIndex(this.bufferIndex);

                    System.out.println("Buffer " + bufferIndex + "; Client with packet: " + packet + " ; Buff Value: " + bufferValue);
                    handlePacket(packet);
                }
            }
            System.out.println("Buffer " + bufferIndex + " cannot make operation ; Packets to send: " + packetsToSend.size() + " Buff Value: " + bufferValue);

            if (!permission) {
                waitForPermission();
                Packet packet = waitForPacketFromBackwardBuffer();

                if (checkPacketLoop(packet)) {
                    handlePacketLoop(packet);
                } else {
                    handlePacket(packet);
                }
            }
            sendPermissionForward();
            sendFirstPacketForward();
        }
    }

    private Alternative prepareAlternative() {
        Guard[] guards = new Guard[clients.getQuantity()];

        for (int i = 0; i < clients.getQuantity(); i++) {
            guards[i] = clients.getClientBufferCommunication(i).getDeliveryChannelIn();
        }
        return new Alternative(guards);
    }

    private void handlePacket(Packet packet) {
        switch (packet.getType()) {
            case PRODUCTION_REQUEST -> handleProductionRequest(packet);
            case CONSUMPTION_REQUEST -> handleConsumptionRequest(packet);
            case PERMISSION -> handlePermissionPacket();
        }
    }

    private void handleProductionRequest(Packet packet) {
        if (productionEnable(packet.getPayload())) {
            production(packet);
        } else {
            packetsToSend.add(packet);
            failureCounter++;
            System.out.println("Buffer " + bufferIndex + " cannot produce ; Packets to send: " + packetsToSend.size() + " Buff Value: " + bufferValue);
        }
    }

    private void handleConsumptionRequest(Packet packet) {
        if (consumptionEnable(packet.getPayload())) {
            consumption(packet);
        } else {
            packetsToSend.add(packet);
            failureCounter++;
            System.out.println("Buffer " + bufferIndex + " cannot consume ; Packets to send: " + packetsToSend.size() + " Buff Value: " + bufferValue);
        }
    }

    private void handlePermissionPacket() {
        System.out.println("Buffer " + bufferIndex + " got permission");
        permission = true;
    }

    private boolean checkPacketLoop(Packet packet) {
        return packet.getBufferIndex() == this.bufferIndex;
    }

    private void handlePacketLoop(Packet packet) {
        System.out.println("Buffer " + bufferIndex + " ; packet loop: " + packet + " Buff Value: " + bufferValue);

        packet.setStatus(PacketStatus.FAILURE);

        packet.getResponseChannel().out().write(packet);
    }

    private boolean productionEnable(int toProduce) {
        return bufferValue + toProduce <= size;
    }

    private boolean consumptionEnable(int toConsume) {
        return bufferValue + toConsume >= 0;
    }

    private void production(Packet packet) {
        System.out.println("Buffer " + bufferIndex + "; Production  " + packet.getPayload() + " ; Buff Value: " + bufferValue);
        bufferValue += packet.getPayload();

        Packet packetResponse = new Packet(PacketType.PRODUCTION_RESPONSE);
        packetResponse.setStatus(PacketStatus.SUCCESS);
        packet.getResponseChannel().out().write(packetResponse);

        successCounter++;
        System.out.println("Buffer " + bufferIndex + "; Production finished ; Buff Value: " + bufferValue);
        showStatusBalance();
    }

    private void consumption(Packet packet) {
        System.out.println("Buffer " + bufferIndex + "; Consumption  " + packet.getPayload() + " ; Buff Value: " + bufferValue);
        this.bufferValue += packet.getPayload();

        Packet packetResponse = new Packet(PacketType.CONSUMPTION_RESPONSE);
        packetResponse.setStatus(PacketStatus.SUCCESS);
        packet.getResponseChannel().out().write(packetResponse);
        successCounter++;
        System.out.println("Buffer " + bufferIndex + "; Consumption finished ; Buff Value: " + bufferValue);
        showStatusBalance();
    }

    private void sendPermissionForward() {
        System.out.println("Buffer " + bufferIndex + " is giving permission forward... ");
        sendPacketForward(new Packet(PacketType.PERMISSION));
        permission = false;
    }

    private void sendFirstPacketForward() {
        Packet packetToSend = packetsToSend.poll();
        sendPacketForward(packetToSend);
    }

    private void sendPacketForward(Packet packet) {
        System.out.println("Buffer " + bufferIndex + " is sending packet: " + packet + " ; Buff Value: " + bufferValue);

        switch (packet.getType()){
            case PERMISSION -> forwardBuffer.writeToInformation(packet);
            default -> forwardBuffer.writeToDelivery(packet);
        }
    }

    private void waitForPermission() {
        System.out.println("Buffer " + bufferIndex + " is waiting for permission from backwardBuffer...");

        Packet packet = null;
        while (packet == null || packet.getType() != PacketType.PERMISSION) {
            packet = backwardBuffer.readFromInformation();
            handlePacket(packet);
        }
    }

    private Packet waitForPacketFromBackwardBuffer() {
        System.out.println("Buffer " + bufferIndex + " is waiting for packet from backwardBuffer...");

        Packet packet = null;
        while (packet == null) {
            packet = backwardBuffer.readFromDelivery();
        }
        return packet;
    }

    public DoubleChannel getForwardBuffer() {
        return forwardBuffer;
    }

    public DoubleChannel getBackwardBuffer() {
        return backwardBuffer;
    }

    public void showStatusBalance() {
        System.out.println("Buffer   " + bufferIndex + "; Balance [SUCCESS : FAILURE] [ " + successCounter + " : " + failureCounter + " ]");
    }
}
