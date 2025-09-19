package com.mcimp.protocol.client;

import java.io.IOException;
import java.io.OutputStream;

import com.mcimp.protocol.ProtocolOutputStream;

public class ClientOutputStream extends ProtocolOutputStream {
    public ClientOutputStream(OutputStream stream) {
        super(stream);
    }

    public void send(ClientPacket packet) throws IOException {
        packet.writeToStream(stream);
    }
}
