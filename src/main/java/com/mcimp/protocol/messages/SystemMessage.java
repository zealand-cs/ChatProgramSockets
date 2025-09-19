package com.mcimp.protocol.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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

        stream.writeUTF(text);
    }

    public static SystemMessage readFromStream(DataInputStream stream) throws IOException {
        var type = SystemMessageLevel.fromByte(stream.readByte());
        var text = stream.readUTF();

        return new SystemMessage(type, text);
    }
}
