package com.mcimp.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class ProtocolInputStream implements AutoCloseable {
    protected DataInputStream stream;

    public ProtocolInputStream(DataInputStream stream) {
        this.stream = stream;
    }

    public ProtocolInputStream(InputStream stream) {
        this(new DataInputStream(stream));
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
