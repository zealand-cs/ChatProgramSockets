package com.mcimp.protocol;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ProtocolOutputStream implements AutoCloseable {
    private DataOutputStream stream;

    public ProtocolOutputStream(DataOutputStream stream) {
        this.stream = stream;
    }

    public ProtocolOutputStream(OutputStream stream) {
        this(new DataOutputStream(stream));
    }

    public void writePacket(Packet packet) throws IOException {
        packet.writeToStream(stream);
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
