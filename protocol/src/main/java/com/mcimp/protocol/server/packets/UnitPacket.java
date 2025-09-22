package com.mcimp.protocol.server.packets;

import java.io.DataOutputStream;
import java.io.IOException;

import com.mcimp.protocol.server.ServerPacket;
import com.mcimp.protocol.server.ServerPacketId;

public class UnitPacket extends ServerPacket {

    public UnitPacket(ServerPacketId packetId) {
        super(packetId);
    }

    @Override
    protected void writeToStreamImpl(DataOutputStream stream) throws IOException {
    }
}
