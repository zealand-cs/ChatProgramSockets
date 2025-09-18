package com.mcimp.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import com.mcimp.protocol.Packet;
import com.mcimp.protocol.PacketType;
import com.mcimp.protocol.ProtocolInputStream;
import com.mcimp.protocol.ProtocolOutputStream;
import com.mcimp.protocol.commands.Command;
import com.mcimp.protocol.commands.CommandType;
import com.mcimp.protocol.commands.JoinCommand;
import com.mcimp.protocol.messages.Message;
import com.mcimp.protocol.messages.MessageType;
import com.mcimp.protocol.messages.SystemMessage;
import com.mcimp.protocol.messages.SystemMessageLevel;
import com.mcimp.protocol.messages.TextMessage;
import com.mcimp.protocol.packets.AuthPacket;
import com.mcimp.protocol.packets.AuthType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    private final Socket socket;
    private final ProtocolInputStream input;
    private final ProtocolOutputStream output;

    private ServerState state;

    private String username;

    public ClientHandler(Socket socket, ServerState state) throws IOException {
        this.socket = socket;
        this.input = new ProtocolInputStream(socket.getInputStream());
        this.output = new ProtocolOutputStream(socket.getOutputStream());
        this.state = state;
    }

    @Override
    public void run() {
        try {
            // Expect a connect packet as the first thing
            Packet connect = input.readPacket();
            assert connect.getType() == PacketType.Connect;
            if (connect.getType() != PacketType.Connect) {
                logger.warn("closing socket: invalid first packet");
                socket.close();
                return;
            }

            logger.info("connection established successfully");

            var welcome = new StringBuilder()
                    .append("Welcome to the server!\n")
                    .append("Login with `/login` or `/register`\n")
                    .append("Joined room ")
                    .append(JoinCommand.DEFAULT_ROOM);

            output.sendInfoMessage(welcome.toString());

            while (true) {
                var packet = input.readPacket();
                handlePacket(packet);
            }

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

    private void handlePacket(Packet packet) throws IOException {
        if (!state.isAuthenticated(socket) && packet.getType() != PacketType.Auth) {
            output.sendInfoMessage("Login with either `/login` or `/register` to do anything");
            return;
        }

        switch (packet.getType()) {
            case PacketType.Connect:
                logger.warn("connect packet received. Socket already connected!");
                break;
            case PacketType.Disconnect:
                state.removeClient(socket);
                socket.close();
                break;
            case PacketType.Auth:
                if (state.isAuthenticated(socket)) {
                    output.sendInfoMessage("You're already authenticated");
                    return;
                }
                var authPacket = (AuthPacket) packet;

                if (authPacket.getAuthType() == AuthType.Login) {
                    if (!state.userExists(authPacket.getUsername())) {
                        logger.warn(authPacket.getUsername() + " doesn't exist");
                        output.sendInfoMessage("Invalid credentials.");
                        return;
                    }

                    var authenticated = state.authenticate(authPacket.getUsername(), authPacket.getPassword());

                    if (!authenticated) {
                        logger.warn(authPacket.getUsername() + " logged in with wrong password");
                        output.sendInfoMessage("Invalid credentials.");
                        return;
                    }
                } else {
                    if (state.userExists(authPacket.getUsername())) {
                        logger.warn(authPacket.getUsername() + " tried to register with existing user");
                        output.sendInfoMessage("User already exists.");
                        return;
                    }

                    state.addAuthSession(authPacket.getUsername(), authPacket.getPassword());
                }
                username = authPacket.getUsername();

                username = authPacket.getUsername();
                state.loginUser(socket, authPacket.getUsername());

                logger.info("{} authenticated successfully", authPacket.getUsername());

                var successMessage = new StringBuilder()
                        .append("Successfully authenticated!\n")
                        .append("Type `/help` to see your possibilities");

                output.sendInfoMessage(successMessage.toString());

                logger.info("sending login info packet");
                break;
            case PacketType.Command:
                var command = (Command) packet;
                handleCommand(command);
                break;
            case PacketType.Message:
                var message = (Message) packet;
                handleMessage(message);
                break;
            case PacketType.Connected, PacketType.Disconnected:
                logger.warn("received packet meant for the client: ", packet.toString());
                break;
            default:
                logger.warn("unhandled packet: ", packet.toString());
                break;
        }
    }

    private void handleCommand(Command command) {
        try {
            switch (command.getCommandType()) {
                case CommandType.Join:
                    var join = (JoinCommand) command;
                    var client = state.getClient(socket);

                    var username = client.getUsername();
                    var socket = client.getSocket();

                    var oldRoom = state.getClientRoom(socket);
                    if (oldRoom.getId().equals(join.getRoomId())) {
                        output.sendInfoMessage("Already in " + join.getRoomId());
                        return;
                    }

                    var newRoom = state.getRoom(join.getRoomId());
                    if (newRoom.isEmpty()) {
                        output.sendInfoMessage(join.getRoomId() + " doesn't exist");
                        return;
                    }

                    state.moveClientToRoom(socket, newRoom.get());

                    logger.info("[{}] {} > [{}] {}", oldRoom.getId(), username, join.getRoomId(), username);

                    oldRoom.broadcastAll(new SystemMessage(SystemMessageLevel.Info, username + " left the room"));

                    newRoom.get()
                            .broadcastAll(new SystemMessage(SystemMessageLevel.Info, username + " joined the room"));
                    break;
                default:
                    logger.error("invalid command packet received: {}", command.toString());
                    break;
            }
        } catch (IOException err) {
            logger.error("error while sending to client: ", err);
        }
    }

    private void handleMessage(Message message) {
        switch (message.getMessageType()) {
            case MessageType.Text:
                var text = (TextMessage) message;
                var room = state.getClientRoom(socket);

                try {
                    room.broadcast(this, text);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                var client = state.getClient(socket);
                logger.info("[{}] {}: {}", room.getId(), client.getUsername(), text.getText());
                break;
            default:
                logger.error("invalid message packet received: {}", message.toString());
                break;
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public ProtocolOutputStream getOutputStream() {
        return output;
    }

    public String getUsername() {
        return username;
    }
}
