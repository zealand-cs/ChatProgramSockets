package com.mcimp.protocol.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SystemMessage extends Message {
    private final static MessageType MESSAGE_TYPE = MessageType.System;

    private SystemMessageLevel level;
    private String text;

    public String getText() {
        return text;
    }

    public SystemMessage(SystemMessageLevel type, String text) {
        super(MESSAGE_TYPE);

        this.level = type;
        this.text = text;
    }

    public static SystemMessage pure(String text) {
        return new SystemMessage(SystemMessageLevel.Pure, text);
    }

    public static SystemMessage info(String text) {
        return new SystemMessage(SystemMessageLevel.Info, text);
    }

    public static SystemMessage success(String text) {
        return new SystemMessage(SystemMessageLevel.Success, text);
    }

    public static SystemMessage warn(String text) {
        return new SystemMessage(SystemMessageLevel.Warning, text);
    }

    public static SystemMessage error(String text) {
        return new SystemMessage(SystemMessageLevel.Error, text);
    }

    @Override
    public void writeToStream(DataOutputStream stream) throws IOException {
        super.writeToStream(stream);

        stream.writeByte(level.toByte());

        stream.writeUTF(text);
    }

    public static SystemMessage readFromStream(DataInputStream stream) throws IOException {
        var type = SystemMessageLevel.fromByte(stream.readByte());
        var text = stream.readUTF();

        return new SystemMessage(type, text);
    }

    public SystemMessageLevel getLevel() {
        return level;
    }
}
