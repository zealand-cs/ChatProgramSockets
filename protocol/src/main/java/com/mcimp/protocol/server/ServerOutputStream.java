package com.mcimp.protocol.server;

import java.io.IOException;
import java.io.OutputStream;

import com.mcimp.protocol.ProtocolOutputStream;

public class ServerOutputStream extends ProtocolOutputStream {
    public ServerOutputStream(OutputStream stream) {
        super(stream);
    }

    public void send(ServerPacket packet) throws IOException {
        packet.writeToStream(stream);
    }
}
