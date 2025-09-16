package com.mcimp.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;

public class Packet {
    private PacketType type;
    private long epochSecond;

    public Packet(PacketType type) {
        this.type = type;
        this.epochSecond = ZonedDateTime.now().toEpochSecond();
    }

    public Packet(PacketType type, long epochSecond) {
        this.type = type;
        this.epochSecond = epochSecond;
    }

    public Packet(PacketType type, ZonedDateTime datetime) {
        this(type, datetime.toEpochSecond());
    }

    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeByte(type.toByte());
        stream.writeLong(epochSecond);
    }

    public static Packet readFromStream(DataInputStream stream) throws IOException {
        var type = PacketType.fromByte(stream.readByte());
        var epochSecond = stream.readLong();

        return new Packet(type, epochSecond);
    }

    public PacketType getType() {
        return type;
    }

    public long getEpochSecond() {
        return epochSecond;
    }
}
