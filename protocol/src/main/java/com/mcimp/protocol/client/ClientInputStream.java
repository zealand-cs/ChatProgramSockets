package com.mcimp.protocol.client;

import java.io.IOException;
import java.io.InputStream;

import com.mcimp.protocol.ProtocolInputStream;

public class ClientInputStream extends ProtocolInputStream {
    public ClientInputStream(InputStream stream) {
        super(stream);
    }

    public ClientPacket read() throws IOException {
        return ClientPacket.readPacket(stream);
    }
}
