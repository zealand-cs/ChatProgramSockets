package com.mcimp.protocol.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FileMessage extends Message {
    private final static MessageType MESSAGE_TYPE = MessageType.File;

    // private String file;

    public FileMessage(String text) {
        super(MESSAGE_TYPE);
    }

    // public FileMessage(Message messagePacket, String text) {
    // super(messagePacket);
    // assert messagePacket.getMessageType() == MESSAGE_TYPE;

    // this.text = text;
    // }

    // @Override
    // public void writeToStream(DataOutputStream stream) throws IOException {
    // super.writeToStream(stream);
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
