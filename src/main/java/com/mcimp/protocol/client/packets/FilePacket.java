package com.mcimp.protocol.client.packets;

import com.mcimp.protocol.client.ClientPacket;
import com.mcimp.protocol.client.ClientPacketId;

// TODO: actually make it work
public class FilePacket {
    private final static ClientPacketId PACKET_ID = ClientPacketId.Connect;

    private String path;

    public FilePacket(String path) {
        // super(PACKET_ID);
    }

    // public FileMessage(Message messagePacket, String text) {
    // super(messagePacket);
    // assert messagePacket.getMessageType() == MESSAGE_TYPE;

    // this.text = text;
    // }

    // @Override
    // public void writeToStream(DataOutputStream stream) throws IOException {
    // stream.writeInt(text.length());
    // stream.writeBytes(text);
    // }

    // public static TextMessage readFromStream(DataInputStream stream) throws
    // IOException {
    // var message = Message.readFromStream(stream);
    // assert message.getMessageType() == MESSAGE_TYPE;

    // var textLength = stream.readInt();
    // var text = new String(stream.readNBytes(textLength));

    // return new TextMessage(message, text);
    // }
}
