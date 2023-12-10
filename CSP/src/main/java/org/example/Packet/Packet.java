package org.example.Packet;

import org.jcsp.lang.One2OneChannel;

import static org.example.Packet.PacketStatus.NOT_SET;

public class Packet {
    private int payload;
    private int bufferIndex;
    private final PacketType type;
    private One2OneChannel responseChannel;
    private PacketStatus status = NOT_SET;

    public Packet(PacketType type) {
        this.type = type;
    }

    public Packet(PacketType type, int payload, int bufferIndex, One2OneChannel responseChannel) {
        this.payload = payload;
        this.type = type;
        this.bufferIndex = bufferIndex;
        this.responseChannel = responseChannel;
    }

    public void setCurrentBufferIndex(int bufferIndex) {
        this.bufferIndex = bufferIndex;
    }


    public void setStatus(PacketStatus status) {
        this.status = status;
    }

    public PacketStatus getStatus() {
        return status;
    }

    public int getPayload() {
        return payload;
    }

    public void setPayload(int payload) {
        this.payload = payload;
    }

    public PacketType getType() {
        return type;
    }

    public int getBufferIndex() {
        return bufferIndex;
    }

    public One2OneChannel getResponseChannel() {
        return responseChannel;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "payload=" + payload +
                ", bufferIndex=" + bufferIndex +
                ", type=" + type +
                '}';
    }

}