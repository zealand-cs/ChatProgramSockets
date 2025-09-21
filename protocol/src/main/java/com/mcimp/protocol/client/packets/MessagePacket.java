package com.mcimp.protocol.client.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mcimp.protocol.client.ClientPacket;
import com.mcimp.protocol.client.ClientPacketId;

public class MessagePacket extends ClientPacket {
    private final static ClientPacketId PACKET_ID = ClientPacketId.Message;

    private String text;

    public MessagePacket(String text) {
        super(PACKET_ID);
        this.text = text;
    }

    @Override
    protected void writeToStreamImpl(DataOutputStream stream) throws IOException {
        stream.writeUTF(text);
    }

    public static MessagePacket readFromStream(DataInputStream stream) throws IOException {
        var text = stream.readUTF();

        return new MessagePacket(text);
    }

    public String getText() {
        return text;
    }
}
