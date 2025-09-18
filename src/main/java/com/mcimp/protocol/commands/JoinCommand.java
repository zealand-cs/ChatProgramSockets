package com.mcimp.protocol.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JoinCommand extends Command {
    private static final CommandType COMMAND_TYPE = CommandType.Join;

    public static final String DEFAULT_ROOM = "global";

    private String roomId;

    public JoinCommand(String roomId) {
        super(COMMAND_TYPE);
        this.roomId = roomId;
    }

    @Override
    public void writeToStream(DataOutputStream stream) throws IOException {
        super.writeToStream(stream);
        stream.writeInt(roomId.length());
        stream.writeBytes(roomId);
    }

    public static JoinCommand readFromStream(DataInputStream stream) throws IOException {
        var roomIdLength = stream.readInt();
        var roomId = new String(stream.readNBytes(roomIdLength), StandardCharsets.UTF_8);

        return new JoinCommand(roomId);
    }

    public String getRoomId() {
        return roomId;
    }
}
