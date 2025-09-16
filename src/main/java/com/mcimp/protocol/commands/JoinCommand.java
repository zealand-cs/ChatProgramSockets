package com.mcimp.protocol.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class JoinCommand extends Command {
    private static final CommandType COMMAND_TYPE = CommandType.Join;
    int roomId;

    public JoinCommand(int roomId) {
        super(COMMAND_TYPE);
        this.roomId = roomId;
    }

    @Override
    public void writeToStream(DataOutputStream stream) throws IOException {
        super.writeToStream(stream);
        stream.writeInt(roomId);
    }

    public static JoinCommand readFromStream(DataInputStream stream) throws IOException {
        var roomId = stream.readInt();
        return new JoinCommand(roomId);
    }
}
