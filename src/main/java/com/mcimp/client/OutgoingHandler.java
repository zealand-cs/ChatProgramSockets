package com.mcimp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mcimp.protocol.ProtocolOutputStream;
import com.mcimp.protocol.commands.JoinCommand;
import com.mcimp.protocol.messages.TextMessage;

public class OutgoingHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(OutgoingHandler.class);

    private final ProtocolOutputStream stream;

    public OutgoingHandler(ProtocolOutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            String line;
            while ((line = reader.readLine().trim()) != null) {
                System.out.print("> ");

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
            default:
                logger.warn("unknown command `{}`", args[0]);
                break;
        }
    }
}
