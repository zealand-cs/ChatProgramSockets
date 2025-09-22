package com.mcimp.protocol.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mcimp.protocol.PacketInterface;
import com.mcimp.protocol.client.packets.AuthenticatePacket;
import com.mcimp.protocol.client.packets.FileDownloadRequestPacket;
import com.mcimp.protocol.client.packets.FileMetadataPacket;
import com.mcimp.protocol.client.packets.JoinRoomPacket;
import com.mcimp.protocol.client.packets.MessagePacket;
import com.mcimp.protocol.client.packets.UnitPacket;

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
            case ClientPacketId.Connect -> new UnitPacket(ClientPacketId.Connect);
            case ClientPacketId.Disconnect -> new UnitPacket(ClientPacketId.Disconnect);
            case ClientPacketId.Authenticate -> AuthenticatePacket.readFromStream(stream);
            case ClientPacketId.JoinRoom -> JoinRoomPacket.readFromStream(stream);
            case ClientPacketId.RoomDetails -> new UnitPacket(ClientPacketId.RoomDetails);
            case ClientPacketId.ListRooms -> new UnitPacket(ClientPacketId.ListRooms);
            case ClientPacketId.ListUsers -> new UnitPacket(ClientPacketId.ListUsers);
            case ClientPacketId.Message -> MessagePacket.readFromStream(stream);

            // File upload is a special case. Returns a unit packets to correctly handle the
            // stream at a later point
            case ClientPacketId.FileMetadata -> FileMetadataPacket.readFromStream(stream);
            case ClientPacketId.FileUpload -> new UnitPacket(ClientPacketId.FileUpload);
            case ClientPacketId.FileDownloadRequest -> FileDownloadRequestPacket.readFromStream(stream);
        };
    }

    public ClientPacketId getType() {
        return type;
    }
}
