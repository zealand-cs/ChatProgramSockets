package com.mcimp.protocol.server;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mcimp.protocol.ProtocolOutputStream;

public class ServerOutputStream extends ProtocolOutputStream {
    private static final Logger logger = LogManager.getLogger(ServerOutputStream.class);

    public ServerOutputStream(OutputStream stream) {
        super(stream);
    }

    public void send(ServerPacket packet) throws IOException {
        logger.debug("sending packet " + packet.getType().getName());
        packet.writeToStream(stream);
    }
}
