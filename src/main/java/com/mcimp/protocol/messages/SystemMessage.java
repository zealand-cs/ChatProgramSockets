package com.mcimp.protocol.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SystemMessage extends Message {
    private final static MessageType MESSAGE_TYPE = MessageType.System;

    private SystemMessageLevel type;
    private String text;

    public String getText() {
        return text;
    }

    public SystemMessage(SystemMessageLevel type, String text) {
        super(MESSAGE_TYPE);

        this.type = type;
        this.text = text;
    }

    @Override
    public void writeToStream(DataOutputStream stream) throws IOException {
        super.writeToStream(stream);

        stream.writeByte(type.toByte());

        stream.writeInt(text.length());
        stream.writeBytes(text);
    }

    public static SystemMessage readFromStream(DataInputStream stream) throws IOException {
        var type = SystemMessageLevel.fromByte(stream.readByte());

        var textLength = stream.readInt();
        var text = new String(stream.readNBytes(textLength), StandardCharsets.UTF_8);

        return new SystemMessage(type, text);
    }
}
