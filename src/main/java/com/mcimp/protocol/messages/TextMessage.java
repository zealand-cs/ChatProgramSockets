package com.mcimp.protocol.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
        stream.writeInt(text.length());
        stream.writeBytes(text);
    }

    public static TextMessage readFromStream(DataInputStream stream) throws IOException {
        var textLength = stream.readInt();
        var text = new String(stream.readNBytes(textLength), StandardCharsets.UTF_8);

        return new TextMessage(text);
    }

    public String getText() {
        return text;
    }
}
