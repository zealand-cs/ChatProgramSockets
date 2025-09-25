package com.mcimp.client;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jline.reader.UserInterruptException;

import com.mcimp.protocol.client.ClientOutputStream;
import com.mcimp.protocol.client.ClientPacketId;
import com.mcimp.protocol.client.packets.AuthenticatePacket;
import com.mcimp.protocol.client.packets.AuthenticationType;
import com.mcimp.protocol.client.packets.FileUploadPacket;
import com.mcimp.protocol.client.packets.FileDownloadRequestPacket;
import com.mcimp.protocol.client.packets.FileMetadataPacket;
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
                    handleCommand(line.substring(1).split(" "));
                    continue;
                }

                terminal.clearPrevLine();

                var message = new MessagePacket(line);
                stream.send(message);
            }
        } catch (UserInterruptException ex) {
            logger.info("server or client disconnected: closing gracefully");
        } catch (IOException e) {
            logger.error("something went totally and completely wrong: ", e);
        }
    }

    private void handleCommand(String[] args) throws IOException {
        var command = args[0];

        switch (command) {
            case "login":
                if (args.length != 2 || args[1] == null) {
                    terminal.writeln("Usage: /login <username>");
                    terminal.flush();
                    return;
                }
                var password = terminal.readLine("password > ", '*');
                var loginAuth = new AuthenticatePacket(AuthenticationType.Login, args[1], password);
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
                if (args.length != 2 || args[1] == null) {
                    terminal.writeln("Usage: /join <room>");
                    terminal.flush();
                    return;
                }
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
            case "upload":
                if (args.length < 2) {
                    terminal.writeln("Usage: /upload <file>");
                    terminal.flush();
                    return;
                }
                // Join together rest of args, since that would just be a filepath with spaces
                var uploadFile = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                stream.send(new FileMetadataPacket(Paths.get(uploadFile)));
                stream.send(new FileUploadPacket(uploadFile));
                break;
            case "download":
                if (args.length < 2) {
                    terminal.writeln("Usage: /download <fileId>");
                    terminal.flush();
                    return;
                }
                var downloadFile = args[1];
                stream.send(new FileDownloadRequestPacket(downloadFile));
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
        terminal.writeln("/login <username>");
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
        terminal.writeln("/upload <file>");
        terminal.writeln("  uploads a file to the server");
        terminal.writeln("/download <fileId>");
        terminal.writeln("  downloads a file from the server to downloads directory");
        terminal.writeln("/help");
        terminal.writeln("  prints this help list");
        terminal.flush();
    }
}
