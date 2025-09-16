package com.mcimp.protocol.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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

    public ConnectedPacket(Packet packet, short userId, String username) {
        super(packet.getType(), packet.getEpochSecond());
        assert packet.getType() == PACKET_TYPE;
        this.userId = userId;
        this.username = username;
    }

    @Override
    public void writeToStream(DataOutputStream stream) throws IOException {
        super.writeToStream(stream);
        stream.writeByte(userId);
        stream.writeByte(username.length());
        stream.writeBytes(username);
    }

    public static ConnectedPacket readFromStream(DataInputStream stream) throws IOException {
        var packet = Packet.readFromStream(stream);
        assert packet.getType() == PACKET_TYPE;

        var userId = stream.readShort();

        var usernameLength = stream.readByte();
        var username = new String(stream.readNBytes(usernameLength));

        return new ConnectedPacket(packet, userId, username);
    }
}
