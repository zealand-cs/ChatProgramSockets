package com.mcimp.protocol.server;

import java.io.IOException;
import java.io.InputStream;

import com.mcimp.protocol.ProtocolInputStream;

public class ServerInputStream extends ProtocolInputStream {
    public ServerInputStream(InputStream stream) {
        super(stream);
    }

    public ServerPacket read() throws IOException {
        return ServerPacket.readPacket(stream);
    }
}
