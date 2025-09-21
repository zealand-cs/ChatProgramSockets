package com.mcimp.protocol;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class ProtocolOutputStream implements AutoCloseable {
    protected DataOutputStream stream;

    public ProtocolOutputStream(DataOutputStream stream) {
        this.stream = stream;
    }

    public ProtocolOutputStream(OutputStream stream) {
        this(new DataOutputStream(stream));
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
