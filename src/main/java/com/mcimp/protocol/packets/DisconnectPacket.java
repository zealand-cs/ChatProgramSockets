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

    public DisconnectPacket(Packet packet) {
        super(packet.getType(), packet.getEpochSecond());
        assert packet.getType() == PACKET_TYPE;
    }

    public static DisconnectPacket readFromStream(DataInputStream stream) throws IOException {
        var packet = Packet.readFromStream(stream);
        assert packet.getType() == PACKET_TYPE;
        return new DisconnectPacket(packet);
    }
}
