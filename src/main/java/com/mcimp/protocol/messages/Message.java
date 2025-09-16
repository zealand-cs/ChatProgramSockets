package com.mcimp.protocol.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mcimp.protocol.Packet;
import com.mcimp.protocol.PacketType;

public class Message extends Packet {
    private final static PacketType PACKET_TYPE = PacketType.Message;

    MessageType messageType;

    public Message(MessageType messageType) {
        super(PACKET_TYPE);
        this.messageType = messageType;
    }

    public Message(Packet packet, MessageType messageType) {
        super(packet.getType(), packet.getEpochSecond());
        assert packet.getType() == PACKET_TYPE;

        this.messageType = messageType;
    }

    public Message(Message message) {
        super(message.getType(), message.getEpochSecond());
        assert message.getType() == PACKET_TYPE;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    @Override
    public void writeToStream(DataOutputStream stream) throws IOException {
        super.writeToStream(stream);
        stream.writeByte(messageType.toByte());
    }

    public static Message readFromStream(DataInputStream stream) throws IOException {
        var packet = Packet.readFromStream(stream);
        assert packet.getType() == PACKET_TYPE;

        var messageType = MessageType.fromByte(stream.readByte());

        return new Message(packet, messageType);
    }
}

class ClientTextMessage {
    String date;
    String text;

    // Client writes
    void writeToStream() {
        // write date
        // write size
        // write text
    }

    // Server reads
    void readFromStream() {
        // read date
        // read size
        // read text
    }
}

class ServerTextMessage {
    String username;
    String date;
    String text;

    // Server writes
    void writeToStream() {
        // write message type

        // write username size
        // write username itself

        // write date
        // write size
        // write text
    }

    // Client reads
    void readFromStream() {
        // read username size
        // read username itself

        // read date
        // read size
        // read text
    }
}
