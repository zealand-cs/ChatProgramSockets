package com.mcimp.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Message extends Packet {
    MessageType messageType;

    public Message(Packet packet, MessageType messageType) {
        super(packet.getType(), packet.getEpochSecond());
        this.messageType = messageType;
    }

    public void writeToStream(DataOutputStream stream) throws IOException {
        super.writeToStream(stream);
        stream.writeByte(messageType.toByte());
    }

    public static Message readFromStream(DataInputStream stream) throws IOException {
        var packet = Packet.readFromStream(stream);
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

class ServerMessageDecoder {
    void decodeStream() {

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
