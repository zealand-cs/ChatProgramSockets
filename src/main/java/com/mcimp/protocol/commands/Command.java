package com.mcimp.protocol.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mcimp.protocol.Packet;
import com.mcimp.protocol.PacketType;

public class Command extends Packet {
    private final static PacketType PACKET_TYPE = PacketType.Command;

    CommandType commandType;

    public Command(CommandType commandType) {
        super(PACKET_TYPE);
        this.commandType = commandType;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    @Override
    public void writeToStream(DataOutputStream stream) throws IOException {
        super.writeToStream(stream);
        stream.writeByte(commandType.toByte());
    }

    public static Command readCommand(DataInputStream stream) throws IOException {
        var type = CommandType.fromByte(stream.readByte());

        return switch (type) {
            case CommandType.Join -> JoinCommand.readFromStream(stream);
        };
    }
}
