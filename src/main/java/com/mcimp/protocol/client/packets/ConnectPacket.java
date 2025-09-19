package com.mcimp.protocol.client.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.mcimp.protocol.client.ClientPacket;
import com.mcimp.protocol.client.ClientPacketId;

public class ConnectPacket extends ClientPacket {
    private final static ClientPacketId PACKET_TYPE = ClientPacketId.Connect;

    public ConnectPacket() {
        super(PACKET_TYPE);
    }

    public static ConnectPacket readFromStream(DataInputStream stream) throws IOException {
        return new ConnectPacket();
    }
}
