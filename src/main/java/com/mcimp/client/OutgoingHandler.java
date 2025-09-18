package com.mcimp.client;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mcimp.protocol.ProtocolOutputStream;
import com.mcimp.protocol.commands.JoinCommand;
import com.mcimp.protocol.messages.TextMessage;
import com.mcimp.protocol.packets.DisconnectPacket;

public class OutgoingHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(OutgoingHandler.class);

    private final ProtocolOutputStream stream;

    private final ClientTerminal terminal;

    public OutgoingHandler(ProtocolOutputStream stream, ClientTerminal terminal) {
        this.stream = stream;
        this.terminal = terminal;
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = terminal.readLine().trim()) != null) {
                if (line.startsWith("/")) {
                    // Substring to remove slash
                    handleCommand(line.substring(1));
                    continue;
                }

                var message = new TextMessage(line);
                stream.writePacket(message);
            }
        } catch (IOException e) {
            logger.error("something went totally and completely wrong: ", e);
        }
    }

    private void handleCommand(String command) throws IOException {
        var args = command.split(" ");

        switch (args[0]) {
            case "join":
                var join = new JoinCommand(args[1]);
                stream.writePacket(join);
                break;
            case "quit", "exit":
                var disconnect = new DisconnectPacket();
                stream.writePacket(disconnect);
                break;
            default:
                logger.warn("unknown command `{}`", args[0]);
                break;
        }
    }
}
