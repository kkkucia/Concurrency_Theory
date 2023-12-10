package org.example.Buffer;

import org.example.Packet.Packet;
import org.jcsp.lang.AltingChannelInput;
import org.jcsp.lang.One2OneChannel;

public class DoubleChannel {
    private final One2OneChannel deliveryChannel;
    private final One2OneChannel informationChannel;

    public DoubleChannel(One2OneChannel deliveryChannel, One2OneChannel informationChannel) {
        this.deliveryChannel = deliveryChannel;
        this.informationChannel = informationChannel;
    }

    public void writeToDelivery(Packet payload) {
        deliveryChannel.out().write(payload);
    }

    public Packet readFromDelivery() {
        return (Packet) deliveryChannel.in().read();
    }

    public void writeToInformation(Packet packet) {
        informationChannel.out().write(packet);
    }

    public Packet readFromInformation() {
        return (Packet) informationChannel.in().read();
    }

    public One2OneChannel getInformationChannel() {
        return informationChannel;
    }

    public AltingChannelInput getDeliveryChannelIn() {
        return deliveryChannel.in();
    }
}
