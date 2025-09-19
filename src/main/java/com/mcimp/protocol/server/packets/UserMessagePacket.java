package com.mcimp.protocol.server.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.mcimp.protocol.server.ServerPacket;
import com.mcimp.protocol.server.ServerPacketId;

public class UserMessagePacket extends ServerPacket {
    private final static ServerPacketId PACKET_ID = ServerPacketId.UserMessage;

    private ZonedDateTime time;
    private String username;
    private String text;

    public UserMessagePacket(ZonedDateTime time, String username, String text) {
        super(PACKET_ID);
        this.time = time;
        this.username = username;
        this.text = text;
    }

    public UserMessagePacket(String username, String text) {
        this(ZonedDateTime.now(), username, text);
    }

    @Override
    protected void writeToStreamImpl(DataOutputStream stream) throws IOException {
        stream.writeLong(time.toEpochSecond());
        stream.writeUTF(username);
        stream.writeUTF(text);
    }

    public static UserMessagePacket readFromStream(DataInputStream stream) throws IOException {
        var epochSecond = stream.readLong();
        var username = stream.readUTF();
        var text = stream.readUTF();

        var instant = Instant.ofEpochSecond(epochSecond);
        var zone = ZoneId.systemDefault();
        var time = ZonedDateTime.ofInstant(instant, zone);

        return new UserMessagePacket(time, username, text);
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }
}
