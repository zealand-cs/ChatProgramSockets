package com.mcimp.protocol.client.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mcimp.protocol.client.ClientPacket;
import com.mcimp.protocol.client.ClientPacketId;

public class JoinRoomPacket extends ClientPacket {
    private final static ClientPacketId PACKET_TYPE = ClientPacketId.JoinRoom;

    private String roomId;

    public JoinRoomPacket(String roomId) {
        super(PACKET_TYPE);
        this.roomId = roomId;
    }

    @Override
    protected void writeToStreamImpl(DataOutputStream stream) throws IOException {
        stream.writeUTF(roomId);
    }

    public static JoinRoomPacket readFromStream(DataInputStream stream) throws IOException {
        var roomId = stream.readUTF();
        return new JoinRoomPacket(roomId);
    }

    public String getRoomId() {
        return roomId;
    }
}
