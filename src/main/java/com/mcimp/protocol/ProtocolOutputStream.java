package com.mcimp.protocol;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.mcimp.protocol.messages.SystemMessage;
import com.mcimp.protocol.messages.SystemMessageLevel;

public class ProtocolOutputStream implements AutoCloseable {
    private DataOutputStream stream;

    public ProtocolOutputStream(DataOutputStream stream) {
        this.stream = stream;
    }

    public ProtocolOutputStream(OutputStream stream) {
        this(new DataOutputStream(stream));
    }

    public void sendSystemMessage(SystemMessageLevel level, String message) throws IOException {
        var packet = new SystemMessage(level, message);
        this.writePacket(packet);
    }

    public void sendInfoMessage(String message) throws IOException {
        this.sendSystemMessage(SystemMessageLevel.Info, message);
    }

    public void writePacket(Packet packet) throws IOException {
        packet.writeToStream(stream);
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
