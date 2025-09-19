package com.mcimp.protocol.client.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.mcimp.protocol.client.ClientPacket;
import com.mcimp.protocol.client.ClientPacketId;

public class DisconnectPacket extends ClientPacket {
    private final static ClientPacketId PACKET_TYPE = ClientPacketId.Disconnect;

    public DisconnectPacket() {
        super(PACKET_TYPE);
    }

    public static DisconnectPacket readFromStream(DataInputStream stream) throws IOException {
        return new DisconnectPacket();
    }
}
