package com.mcimp.protocol;

public class DisconnectedPacket extends Packet {
    private short userId;

    public DisconnectedPacket(Packet packet, short userId) {
        super(packet.getType(), packet.getEpochSecond());
        this.userId = userId;
    }
}
