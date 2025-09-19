package com.mcimp.protocol.server.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mcimp.protocol.server.ServerPacket;
import com.mcimp.protocol.server.ServerPacketId;

public class DisconnectedPacket extends ServerPacket {
    private final static ServerPacketId PACKET_TYPE = ServerPacketId.Disconnected;

    private short userId;
    private String username;

    public DisconnectedPacket(short userId, String username) {
        super(PACKET_TYPE);
        this.userId = userId;
        this.username = username;
    }

    @Override
    protected void writeToStreamImpl(DataOutputStream stream) throws IOException {
        stream.writeShort(userId);
        stream.writeUTF(username);
    }

    public static DisconnectedPacket readFromStream(DataInputStream stream) throws IOException {
        var userId = stream.readShort();
        var username = stream.readUTF();

        return new DisconnectedPacket(userId, username);
    }
}
