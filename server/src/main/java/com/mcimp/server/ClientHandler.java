package com.mcimp.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import com.mcimp.server.utils.EmojiReplacer;
import com.mcimp.protocol.client.ClientInputStream;
import com.mcimp.protocol.client.ClientPacket;
import com.mcimp.protocol.client.ClientPacketId;
import com.mcimp.protocol.client.packets.AuthenticatePacket;
import com.mcimp.protocol.client.packets.AuthenticationType;
import com.mcimp.protocol.client.packets.FileUploadPacket;
import com.mcimp.protocol.client.packets.JoinRoomPacket;
import com.mcimp.protocol.client.packets.MessagePacket;
import com.mcimp.protocol.server.ServerOutputStream;
import com.mcimp.protocol.server.packets.SystemMessagePacket;
import com.mcimp.protocol.server.packets.UserMessagePacket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    private final EmojiReplacer replacer;

    private final Socket socket;
    private final ClientInputStream input;
    private final ServerOutputStream output;

    private ServerState state;

    private String username;

    public ClientHandler(Socket socket, ServerState state, EmojiReplacer replacer) throws IOException {
        this.socket = socket;
        this.input = new ClientInputStream(socket.getInputStream());
        this.output = new ServerOutputStream(socket.getOutputStream());
        this.state = state;

        this.replacer = replacer;
    }

    @Override
    public void run() {
        try {
            // Expect a connect packet as the first thing
            ClientPacket connect = input.read();
            assert connect.getType() == ClientPacketId.Connect;
            if (connect.getType() != ClientPacketId.Connect) {
                logger.warn("closing socket: invalid first packet");
                socket.close();
                return;
            }

            logger.info("connection established successfully");

            output.send(SystemMessagePacket.builder().info().user().text("Welcome to the server!").build());
            output.send(SystemMessagePacket.builder().info().user().text("Login with `/login` or `/register`").build());

            while (true) {
                var packet = input.read();
                handlePacket(packet);
            }

        } catch (EOFException ex) {
            state.removeClient(socket);
            logger.info("socket disconnected");
        } catch (SocketException ex) {
            logger.info("socket to client closed");
        } catch (IOException e) {
            logger.error("socket io error: ", e);
        } finally {
            try {
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handlePacket(ClientPacket packet) throws IOException {
        if (!state.isAuthenticated(socket) && packet.getType() != ClientPacketId.Authenticate
                && packet.getType() != ClientPacketId.Disconnect) {
            output.send(SystemMessagePacket.builder().warn().user()
                    .text("Login with either `/login` or `/register` to do anything").build());
            return;
        }

        switch (packet.getType()) {
            case ClientPacketId.Connect:
                logger.warn("connect packet received. Socket already connected!");
                break;
            case ClientPacketId.Disconnect:
                state.removeClient(socket);
                socket.close();
                break;
            case ClientPacketId.Authenticate:
                handleAuthenticate((AuthenticatePacket) packet);
                break;
            case ClientPacketId.JoinRoom:
                handleJoinRoom((JoinRoomPacket) packet);
                break;
            case ClientPacketId.RoomDetails:
                handleRoomDetails();
                break;
            case ClientPacketId.ListRooms:
                handleListRooms();
                break;
            case ClientPacketId.ListUsers:
                handleListUsers();
                break;
            case ClientPacketId.Message:
                handleMessage((MessagePacket) packet);
                break;
            case ClientPacketId.FileUpload:
                handleFileUpload(packet);
                break;
            default:
                logger.warn("unhandled packet: ", packet.toString());
                break;
        }
    }

    private void handleAuthenticate(AuthenticatePacket authPacket) throws IOException {
        if (state.isAuthenticated(socket)) {
            output.send(SystemMessagePacket.builder().info().user().text("You're already authenticated!").build());
            return;
        }

        if (authPacket.getAuthType() == AuthenticationType.Login) {
            if (!state.userExists(authPacket.getUsername())) {
                logger.warn(authPacket.getUsername() + " doesn't exist");
                output.send(SystemMessagePacket.builder().error().user().text("Invalid credentials.").build());
                return;
            }

            var authenticated = state.authenticate(authPacket.getUsername(), authPacket.getPassword());

            if (!authenticated) {
                logger.warn(authPacket.getUsername() + " logged in with wrong password");
                output.send(SystemMessagePacket.builder().error().user().text("Invalid credentials.").build());
                return;
            }
        } else {
            if (state.userExists(authPacket.getUsername())) {
                logger.warn(authPacket.getUsername() + " tried to register with existing user");
                output.send(SystemMessagePacket.builder().error().user().text("Username already taken").build());
                return;
            }

            state.addAuthSession(authPacket.getUsername(), authPacket.getPassword());
        }

        username = authPacket.getUsername();
        state.loginUser(socket, authPacket.getUsername());

        logger.info("{} authenticated successfully", authPacket.getUsername());

        output.send(SystemMessagePacket.builder().success().user().text("Successfully authenticated!").build());
        output.send(SystemMessagePacket.builder().info().user().text("Type `/help` to see your possibilities").build());

        var room = state.getClientRoom(socket);
        room.broadcast(SystemMessagePacket.builder().info().user().text(username + " logged in!").build());

        logger.info("sending login info packet");
    }

    private void handleFileUpload(ClientPacket packet) throws IOException {
        logger.info("receiving file from client " + username);

        var fileId = state.getFileRepository().createFileId();
        var fileStream = state.getFileRepository().fileStream(fileId);

        // Currently stops all UI on server terminal propably because System.out gets
        // blocked or something
        FileUploadPacket.readInputStreamToStream(input.getInnerStream(), fileStream);
    }

    private void handleJoinRoom(JoinRoomPacket join) throws IOException {
        var builder = SystemMessagePacket.builder();

        var client = state.getClient(socket);

        var username = client.getUsername();
        var socket = client.getSocket();

        var oldRoom = state.getClientRoom(socket);
        if (oldRoom.getId().equals(join.getRoomId())) {
            output.send(builder.info().user().text("Already connected to " + join.getRoomId()).build());
            return;
        }

        Room newRoom;
        var targetRoom = state.getRoom(join.getRoomId());
        if (targetRoom.isEmpty()) {
            newRoom = state.createRoom(join.getRoomId());
            output.send(builder.info().user().text("Created new room " + join.getRoomId()).build());
        } else {
            newRoom = targetRoom.get();
        }

        state.moveClientToRoom(socket, newRoom);

        oldRoom.broadcast(builder.info().user().text(username + " left the room").build());
        logger.info("[{}] {} > [{}] {}", oldRoom.getId(), username, join.getRoomId(), username);

        newRoom.broadcast(builder.info().user().text(username + " joined the room").build());
    }

    private void handleRoomDetails() throws IOException {
        var room = state.getClientRoom(socket);

        var sb = new StringBuilder("Room details:")
                .append("\nRoom: ")
                .append(room.getId())
                .append("\nUsers:");

        for (var client : room.getClients()) {
            if (client.username != null) {
                sb.append("\n- ").append(client.username);
            }
        }
        sb.append("\nTotal: ").append(room.getClients().size());

        var packet = SystemMessagePacket.builder().info().user().text(sb.toString()).build();
        output.send(packet);
    }

    private void handleListRooms() throws IOException {
        var rooms = state.getRooms();

        var sb = new StringBuilder("The server has the following rooms:");
        for (var room : rooms) {
            sb.append("\n- ").append(room.getId());
        }

        var packet = SystemMessagePacket.builder().info().user().text(sb.toString()).build();
        output.send(packet);
    }

    private void handleListUsers() throws IOException {
        var usernames = state.getAuthenticatedUsers();
        var clients = state.getClients();

        var builder = SystemMessagePacket.builder();
        var sb = new StringBuilder("Users on the server:");

        for (var username : usernames) {
            sb.append("\n- ").append(username);
        }

        sb.append("\nClients not logged in: ").append(clients.size() - usernames.size());

        var packet = builder.info().user().text(sb.toString()).build();
        output.send(packet);
    }

    private void handleMessage(MessagePacket packet) {
        var room = state.getClientRoom(socket);

        var text = replacer.replaceEmojis(packet.getText());
        var roomPacket = new UserMessagePacket(username, room.getId(), text);

        try {
            room.broadcast(roomPacket, this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("[{}] {}: {}", room.getId(), username, text);
    }

    public Socket getSocket() {
        return socket;
    }

    public ServerOutputStream getOutputStream() {
        return output;
    }

    public String getUsername() {
        return username;
    }
}
