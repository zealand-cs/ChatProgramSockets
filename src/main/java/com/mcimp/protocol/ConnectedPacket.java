package com.mcimp.protocol;

public class ConnectedPacket extends Packet {
    private short userId;
    private String username;

    public ConnectedPacket(Packet packet, short userId, String username) {
        super(packet.getType(), packet.getEpochSecond());
        this.userId = userId;
        this.username = username;
    }
}
