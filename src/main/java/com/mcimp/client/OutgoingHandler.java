package com.mcimp.client;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jline.reader.UserInterruptException;

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
                stream.send(message);
            }
        } catch (UserInterruptException ex) {
            logger.info("server or client disconnected: closing gracefully");
        } catch (IOException e) {
            logger.error("something went totally and completely wrong: ", e);
        }
    }

    private void handleCommand(String command) throws IOException {
        var args = command.split(" ");

        switch (args[0]) {
            case "join":
                var join = new JoinCommand(args[1]);
                stream.send(join);
                break;
            case "login":
                if (args.length != 3 || args[1] == null || args[2] == null) {
                    terminal.writeln("Usage: /login <username> <password>");
                    terminal.flush();
                    return;
                }
                var loginAuth = new AuthPacket(AuthType.Login, args[1], args[2]);
                stream.send(loginAuth);
                break;
            case "register":
                if (args.length != 3 || args[1] == null || args[2] == null) {
                    terminal.writeln("Usage: /register <username> <password>");
                    terminal.flush();
                    return;
                }
                var registerAuth = new AuthPacket(AuthType.Register, args[1], args[2]);
                stream.send(registerAuth);
                break;
            case "quit", "exit":
                var disconnect = new DisconnectPacket();
                stream.send(disconnect);
                break;
            default:
                logger.warn("unknown command `{}`", args[0]);
                break;
        }
    }
}
