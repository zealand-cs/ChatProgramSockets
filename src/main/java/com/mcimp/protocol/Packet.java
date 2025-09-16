package com.mcimp.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mcimp.protocol.commands.Command;
import com.mcimp.protocol.messages.Message;
import com.mcimp.protocol.packets.AuthPacket;
import com.mcimp.protocol.packets.ConnectPacket;
import com.mcimp.protocol.packets.ConnectedPacket;
import com.mcimp.protocol.packets.DisconnectPacket;
import com.mcimp.protocol.packets.DisconnectedPacket;

public abstract class Packet {
    private PacketType type;

    public Packet(PacketType type) {
        this.type = type;
    }

    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeByte(type.toByte());
    }

    public static Packet readPacket(DataInputStream stream) throws IOException {
        var type = PacketType.fromByte(stream.readByte());

        return switch (type) {
            case PacketType.Connect -> ConnectPacket.readFromStream(stream);
            case PacketType.Connected -> ConnectedPacket.readFromStream(stream);
            case PacketType.Disconnect -> DisconnectPacket.readFromStream(stream);
            case PacketType.Disconnected -> DisconnectedPacket.readFromStream(stream);
            case PacketType.Auth -> AuthPacket.readFromStream(stream);
            case PacketType.Banned, PacketType.Unbanned -> throw new RuntimeException("ban and unban not implemented");
            case PacketType.Command -> Command.readCommand(stream);
            case PacketType.Message -> Message.readMessage(stream);
        };
    }

    public PacketType getType() {
        return type;
    }
}
