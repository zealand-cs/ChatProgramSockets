package com.mcimp.protocol.server.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mcimp.protocol.server.ServerPacket;
import com.mcimp.protocol.server.ServerPacketId;


public class SystemMessagePacket extends ServerPacket {
    private final static ServerPacketId PACKET_ID = ServerPacketId.SystemMessage;

    private SystemMessageScope scope;
    private SystemMessageLevel level;
    private String text;

    public SystemMessagePacket(SystemMessageScope scope, SystemMessageLevel level, String text) {
        super(PACKET_ID);

        this.scope = scope;
        this.level = level;
        this.text = text;
    }

    public static SystemMessagePacketBuilder builder() {
        return new SystemMessagePacketBuilder();
    }

    @Override
    protected void writeToStreamImpl(DataOutputStream stream) throws IOException {
        stream.writeByte(scope.toByte());
        stream.writeByte(level.toByte());
        stream.writeUTF(text);
    }

    public static SystemMessagePacket readFromStream(DataInputStream stream) throws IOException {
        var scope = SystemMessageScope.fromByte(stream.readByte());
        var type = SystemMessageLevel.fromByte(stream.readByte());
        var text = stream.readUTF();

        return new SystemMessagePacket(scope, type, text);
    }

    public SystemMessageScope getScope() {
        return scope;
    }

    public SystemMessageLevel getLevel() {
        return level;
    }

    public String getText() {
        return text;
    }
}
