package com.mcimp.protocol.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mcimp.protocol.Packet;
import com.mcimp.protocol.PacketType;

public class DisconnectedPacket extends Packet {
    private final static PacketType PACKET_TYPE = PacketType.Disconnected;

    private short userId;
    private String username;

    public DisconnectedPacket(short userId, String username) {
        super(PACKET_TYPE);
        this.userId = userId;
        this.username = username;
    }

    public DisconnectedPacket(Packet packet, short userId, String username) {
        super(packet.getType(), packet.getEpochSecond());
        assert packet.getType() == PACKET_TYPE;

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

    public static DisconnectedPacket readFromStream(DataInputStream stream) throws IOException {
        var packet = Packet.readFromStream(stream);
        assert packet.getType() == PACKET_TYPE;

        var userId = stream.readShort();

        var usernameLength = stream.readInt();
        var username = new String(stream.readNBytes(usernameLength));

        return new DisconnectedPacket(packet, userId, username);
    }
}
