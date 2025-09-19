package com.mcimp.protocol.client.packets;

import java.io.DataOutputStream;
import java.io.IOException;

import com.mcimp.protocol.client.ClientPacket;
import com.mcimp.protocol.client.ClientPacketId;

public class UnitPacket extends ClientPacket {

    public UnitPacket(ClientPacketId packetId) {
        super(packetId);
    }

    @Override
    protected void writeToStreamImpl(DataOutputStream stream) throws IOException {
    }
}
