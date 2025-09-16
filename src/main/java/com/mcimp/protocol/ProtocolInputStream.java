package com.mcimp.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProtocolInputStream implements AutoCloseable {
    private DataInputStream stream;

    public ProtocolInputStream(DataInputStream stream) {
        this.stream = stream;
    }

    public ProtocolInputStream(InputStream stream) {
        this(new DataInputStream(stream));
    }


    public Packet readPacket() throws IOException {
        return Packet.readPacket(stream);
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
