package com.mcimp.protocol.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.mcimp.protocol.Packet;
import com.mcimp.protocol.PacketType;

public class DisconnectPacket extends Packet {
    private final static PacketType PACKET_TYPE = PacketType.Disconnect;

    public DisconnectPacket() {
        super(PACKET_TYPE);
    }

    public static DisconnectPacket readFromStream(DataInputStream stream) throws IOException {
        return new DisconnectPacket();
    }
}
