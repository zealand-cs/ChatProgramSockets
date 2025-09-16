package com.mcimp.protocol.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.mcimp.protocol.Packet;
import com.mcimp.protocol.PacketType;

public class ConnectedPacket extends Packet {
    private final static PacketType PACKET_TYPE = PacketType.Connected;

    private short userId;
    private String username;

    public ConnectedPacket(short userId, String username) {
        super(PACKET_TYPE);
        this.userId = userId;
        this.username = username;
    }

    @Override
    public void writeToStream(DataOutputStream stream) throws IOException {
        super.writeToStream(stream);
        stream.writeShort(userId);
        stream.writeInt(username.length());
        stream.writeBytes(username);
    }

    public static ConnectedPacket readFromStream(DataInputStream stream) throws IOException {
        var userId = stream.readShort();

        var usernameLength = stream.readInt();
        var username = new String(stream.readNBytes(usernameLength), StandardCharsets.UTF_8);

        return new ConnectedPacket(userId, username);
    }
}
