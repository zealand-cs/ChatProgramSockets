package com.mcimp.client;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jline.reader.UserInterruptException;

import com.mcimp.protocol.client.ClientOutputStream;
import com.mcimp.protocol.client.ClientPacketId;
import com.mcimp.protocol.client.packets.AuthenticatePacket;
import com.mcimp.protocol.client.packets.AuthenticationType;
import com.mcimp.protocol.client.packets.JoinRoomPacket;
import com.mcimp.protocol.client.packets.MessagePacket;
import com.mcimp.protocol.client.packets.UnitPacket;

public class OutgoingHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(OutgoingHandler.class);

    private final ClientOutputStream stream;

    private final ClientTerminal terminal;

    public OutgoingHandler(ClientOutputStream stream, ClientTerminal terminal) {
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

                var message = new MessagePacket(line);
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
            case "login":
                if (args.length != 3 || args[1] == null || args[2] == null) {
                    terminal.writeln("Usage: /login <username> <password>");
                    terminal.flush();
                    return;
                }
                var loginAuth = new AuthenticatePacket(AuthenticationType.Login, args[1], args[2]);
                stream.send(loginAuth);
                break;
            case "register":
                if (args.length != 3 || args[1] == null || args[2] == null) {
                    terminal.writeln("Usage: /register <username> <password>");
                    terminal.flush();
                    return;
                }
                var registerAuth = new AuthenticatePacket(AuthenticationType.Register, args[1], args[2]);
                stream.send(registerAuth);
                break;
            case "logout":
                var disconnect = new UnitPacket(ClientPacketId.Disconnect);
                stream.send(disconnect);
                break;
            case "join":
                var join = new JoinRoomPacket(args[1]);
                stream.send(join);
                break;
            case "room":
                stream.send(new UnitPacket(ClientPacketId.RoomDetails));
                break;
            case "rooms":
                stream.send(new UnitPacket(ClientPacketId.ListRooms));
                break;
            case "users":
                stream.send(new UnitPacket(ClientPacketId.ListUsers));
                break;
            case "help":
                printHelp();
                break;
            default:
                logger.warn("unknown command `{}`", args[0]);
                break;
        }
    }

    private void printHelp() {
        terminal.writeln("/login <username> <password>");
        terminal.writeln("  logs in to a specific user");
        terminal.writeln("/register <username> <password>");
        terminal.writeln("  registers a new user on the server");
        terminal.writeln("/logout");
        terminal.writeln("  logs out of the server");
        terminal.writeln("/join <room>");
        terminal.writeln("  joins a room");
        terminal.writeln("/room");
        terminal.writeln("  lists details about your current room");
        terminal.writeln("/rooms");
        terminal.writeln("  lists all rooms on the server");
        terminal.writeln("/users");
        terminal.writeln("  lists users on the server");
        terminal.writeln("/help");
        terminal.writeln("  prints this help list");
        terminal.flush();
    }
}
