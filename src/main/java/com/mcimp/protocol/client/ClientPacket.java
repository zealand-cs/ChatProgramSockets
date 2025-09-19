package com.mcimp.protocol.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mcimp.protocol.PacketInterface;
import com.mcimp.protocol.client.packets.AuthenticatePacket;
import com.mcimp.protocol.client.packets.ConnectPacket;
import com.mcimp.protocol.client.packets.DisconnectPacket;
import com.mcimp.protocol.client.packets.JoinRoomPacket;
import com.mcimp.protocol.client.packets.MessagePacket;

public abstract class ClientPacket implements PacketInterface {
    private ClientPacketId type;

    public ClientPacket(ClientPacketId type) {
        this.type = type;
    }

    public final void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeByte(type.toByte());
        writeToStreamImpl(stream);
    }

    protected abstract void writeToStreamImpl(DataOutputStream stream) throws IOException;

    public static ClientPacket readPacket(DataInputStream stream) throws IOException {
        var type = ClientPacketId.fromByte(stream.readByte());

        return switch (type) {
            case ClientPacketId.Connect -> ConnectPacket.readFromStream(stream);
            case ClientPacketId.Disconnect -> DisconnectPacket.readFromStream(stream);
            case ClientPacketId.Authenticate -> AuthenticatePacket.readFromStream(stream);
            case ClientPacketId.JoinRoom -> JoinRoomPacket.readFromStream(stream);
            case ClientPacketId.Message -> MessagePacket.readFromStream(stream);
        };
    }

    public ClientPacketId getType() {
        return type;
    }
}
