package com.mcimp.client;

import java.io.EOFException;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.mcimp.protocol.PacketType;
import com.mcimp.protocol.ProtocolInputStream;
import com.mcimp.protocol.messages.Message;
import com.mcimp.protocol.messages.MessageType;
import com.mcimp.protocol.messages.SystemMessage;

public class IncomingHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(IncomingHandler.class);

    private final ProtocolInputStream stream;

    public IncomingHandler(ProtocolInputStream stream) {
        this.stream = stream;
    }

    @Override
    public void run() {
        // Outer try catch to stop if the server stops before the client
        // When the same catch is in the loop, it will keep on error'ing,
        // very quickly indeed.
        // Ooohhhh how we loooove nesting...


        try {
            while (true) {
                try {
                    var packet = stream.readPacket();

                    switch (packet.getType()) {
                        case PacketType.Connected:
                            logger.info("Server: " + packet);
                            break;
                        case PacketType.Message:
                            handleMessage((Message) packet);
                            break;
                        case PacketType.Connect, PacketType.Disconnect, PacketType.Auth, PacketType.Command:
                            logger.warn("received packet meant for the server: ", packet.toString());
                        default:
                            logger.warn("unhandled packet: ", packet.toString());
                    }
                } catch (EOFException e) {}
            }
        } catch (IOException e) {
            logger.error("error while reading from server: " + e);
        }
    }

    private void handleMessage(Message message) {
        switch (message.getMessageType()) {
            case MessageType.System:
                var systemMessage = (SystemMessage) message;
                System.out.println("Server: " + systemMessage.getText());
                break;
            default:
                logger.warn("unhandled message type: " + message.toString());
        }
    }
}
