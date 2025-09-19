package com.mcimp.protocol.server.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.mcimp.protocol.server.ServerPacket;
import com.mcimp.protocol.server.ServerPacketId;

public class SystemMessagePacket extends ServerPacket {
    private final static ServerPacketId PACKET_ID = ServerPacketId.SystemMessage;

    private ZonedDateTime time;
    private SystemMessageScope scope;
    private SystemMessageLevel level;
    private String text;

    public SystemMessagePacket(ZonedDateTime time, SystemMessageScope scope, SystemMessageLevel level, String text) {
        super(PACKET_ID);

        this.time = time;
        this.scope = scope;
        this.level = level;
        this.text = text;
    }

    public SystemMessagePacket(SystemMessageScope scope, SystemMessageLevel level, String text) {
        this(ZonedDateTime.now(), scope, level, text);
    }

    public static SystemMessagePacketBuilder builder() {
        return new SystemMessagePacketBuilder();
    }

    @Override
    protected void writeToStreamImpl(DataOutputStream stream) throws IOException {
        stream.writeLong(time.toEpochSecond());
        stream.writeByte(scope.toByte());
        stream.writeByte(level.toByte());
        stream.writeUTF(text);
    }

    public static SystemMessagePacket readFromStream(DataInputStream stream) throws IOException {
        var epochSecond = stream.readLong();
        var scope = SystemMessageScope.fromByte(stream.readByte());
        var type = SystemMessageLevel.fromByte(stream.readByte());
        var text = stream.readUTF();

        var instant = Instant.ofEpochSecond(epochSecond);
        var zone = ZoneId.systemDefault();
        var time = ZonedDateTime.ofInstant(instant, zone);

        return new SystemMessagePacket(time, scope, type, text);
    }

    public ZonedDateTime getTime() {
        return time;
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
