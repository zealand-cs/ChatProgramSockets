package com.mcimp.client;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.mcimp.protocol.PacketType;
import com.mcimp.protocol.ProtocolInputStream;

public class IncomingHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(Client.class);

    private ProtocolInputStream stream;

    public IncomingHandler(ProtocolInputStream stream) {
        this.stream = stream;
    }

    @Override
    public void run() {
        try {
            while (true) {
                var packet = stream.readPacket();

                switch (packet.getType()) {
                    case PacketType.Connect:
                        System.out.println("SERVER: not implemented");
                        break;
                    case PacketType.Connected:
                        logger.info("SYSTEM: " + packet);
                    default:
                        logger.warn("unhandled packet: " + packet.toString());
                }

            }
        } catch (IOException e) {
            logger.error("error while reading from server: " + e);
        }
    }
}
