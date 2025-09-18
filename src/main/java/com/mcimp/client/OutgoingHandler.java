package com.mcimp.client;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mcimp.protocol.ProtocolOutputStream;
import com.mcimp.protocol.commands.JoinCommand;
import com.mcimp.protocol.messages.TextMessage;
import com.mcimp.protocol.packets.AuthPacket;
import com.mcimp.protocol.packets.AuthType;
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
            case "login":
                if (args.length != 3 || args[1] == null || args[2] == null) {
                    terminal.writeln("Usage: /login <username> <password>");
                    terminal.flush();
                    return;
                }
                var loginAuth = new AuthPacket(AuthType.Login, args[1], args[2]);
                stream.writePacket(loginAuth);
                stream.writePacket(new JoinCommand(JoinCommand.DEFAULT_ROOM));
                break;
            case "register":
                if (args.length != 3 || args[1] == null || args[2] == null) {
                    terminal.writeln("Usage: /register <username> <password>");
                    terminal.flush();
                    return;
                }
                var registerAuth = new AuthPacket(AuthType.Register, args[1], args[2]);
                stream.writePacket(registerAuth);
                stream.writePacket(new JoinCommand(JoinCommand.DEFAULT_ROOM));
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
