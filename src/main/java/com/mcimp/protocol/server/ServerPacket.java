package com.mcimp.protocol.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mcimp.protocol.server.packets.ConnectedPacket;
import com.mcimp.protocol.server.packets.DisconnectedPacket;
import com.mcimp.protocol.server.packets.SystemMessagePacket;
import com.mcimp.protocol.server.packets.UserMessagePacket;

public abstract class ServerPacket {
    private ServerPacketId type;

    public ServerPacket(ServerPacketId type) {
        this.type = type;
    }

    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeByte(type.toByte());
    }

    public static ServerPacket readPacket(DataInputStream stream) throws IOException {
        var type = ServerPacketId.fromByte(stream.readByte());

        return switch (type) {
            case ServerPacketId.Connected -> ConnectedPacket.readFromStream(stream);
            case ServerPacketId.Disconnected -> DisconnectedPacket.readFromStream(stream);
            case ServerPacketId.SystemMessage -> SystemMessagePacket.readFromStream(stream);
            case ServerPacketId.UserMessage -> UserMessagePacket.readFromStream(stream);
        };
    }

    public ServerPacketId getType() {
        return type;
    }
}
