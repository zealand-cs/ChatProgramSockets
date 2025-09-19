package com.mcimp.protocol.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TextMessage extends Message {
    private final static MessageType MESSAGE_TYPE = MessageType.Text;
    private String text;

    public TextMessage(String text) {
        super(MESSAGE_TYPE);
        this.text = text;
    }

    @Override
    public void writeToStream(DataOutputStream stream) throws IOException {
        super.writeToStream(stream);
        stream.writeUTF(text);
    }

    public static TextMessage readFromStream(DataInputStream stream) throws IOException {
        var text = stream.readUTF();

        return new TextMessage(text);
    }

    public String getText() {
        return text;
    }
}
