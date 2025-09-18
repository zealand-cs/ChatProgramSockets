package com.mcimp.server;

import java.io.*;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.mcimp.protocol.Packet;
import com.mcimp.protocol.PacketType;
import com.mcimp.protocol.ProtocolInputStream;
import com.mcimp.protocol.ProtocolOutputStream;
import com.mcimp.protocol.commands.Command;
import com.mcimp.protocol.commands.CommandType;
import com.mcimp.protocol.commands.JoinCommand;
import com.mcimp.protocol.messages.Message;
import com.mcimp.protocol.messages.MessageType;
import com.mcimp.protocol.messages.TextMessage;
import com.mcimp.protocol.packets.AuthPacket;
import com.mcimp.protocol.packets.AuthType;
import com.mcimp.repository.UserRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ClientHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    private Socket socket;
    private ServerState state;

    private Set<String> loggedInUsers = ConcurrentHashMap.newKeySet();
    private UserRepository repo;

    private String username;

    public ClientHandler(Socket socket, ServerState state, UserRepository repo, Set<String> loggedInUsers) {
        this.socket = socket;
        this.repo = repo;
        this.loggedInUsers = loggedInUsers;
        this.state = state;
    }

    @Override
    public void run() {
        try (
                ProtocolInputStream input = new ProtocolInputStream(socket.getInputStream());
                ProtocolOutputStream output = new ProtocolOutputStream(socket.getOutputStream())) {

            // Expect a connect packet as the first thing
            Packet connect = input.readPacket();
            assert connect.getType() == PacketType.Connect;
            if (connect.getType() != PacketType.Connect) {
                logger.warn("closing socket: invalid first packet");
                socket.close();
                return;
            }

            logger.info("connection established successfully");

            output.sendInfoMessage("Welcome to the server!");

            var authPacket = (AuthPacket) input.readPacket();
            assert authPacket.getType() == PacketType.Auth;

            if (authPacket.getAuthType() == AuthType.Login) {
                if (!repo.userExists(authPacket.getUsername())) {
                    logger.warn(authPacket.getUsername() + " doesn't exist");
                    output.sendInfoMessage("Invalid credentials.");
                    socket.close();
                    return;
                }

                var authenticated = repo.authenticate(authPacket.getUsername(), authPacket.getPassword());

                if (!authenticated) {
                    logger.warn(authPacket.getUsername() + " logged in with wrong password");
                    output.sendInfoMessage("Invalid credentials.");
                    socket.close();
                    return;
                }
            } else {
                if (repo.userExists(authPacket.getUsername())) {
                    logger.warn(authPacket.getUsername() + " tried to register with existing user");
                    output.sendInfoMessage("User already exists.");
                    socket.close();
                    return;
                }

                repo.addUser(authPacket.getUsername(), authPacket.getPassword());
            }

            username = authPacket.getUsername();
            loggedInUsers.add(authPacket.getUsername());
            logger.info("{} logged in successfully", authPacket.getUsername());

            var builder = new StringBuilder("To begin chatting with someone, `/join` a room!\n");
            for (var room : state.getRooms().values()) {
                builder.append(room.getId() + " - " + room.getName());
            }

            output.sendInfoMessage(builder.toString());
            logger.info("sending info packet");

            while (true) {
                var packet = input.readPacket();
                switch (packet.getType()) {
                    case PacketType.Connect:
                        logger.warn("connect packet received. Socket already connected!");
                        break;
                    case PacketType.Command:
                        var command = (Command) packet;
                        handleCommand(command, output);
                        break;
                    case PacketType.Message:
                        var message = (Message) packet;
                        handleMessage(message, output);
                        break;
                    case PacketType.Connected, PacketType.Disconnected:
                        logger.warn("received packet meant for the client: ", packet.toString());
                        break;
                    default:
                        logger.warn("unhandled packet: ", packet.toString());
                        break;
                }
            }

        } catch (IOException e) {
            logger.error("socket io error: ", e);
        }
    }

    private void handleCommand(Command command, ProtocolOutputStream output) {
        try {
            switch (command.getCommandType()) {
                case CommandType.Join:
                    var join = (JoinCommand) command;
                    var clientId = state.getClientId(socket);
                    if (clientId == null) {
                        System.out.println("client id not found");
                    }
                    state.moveClientToRoom(clientId, join.getRoomId());
                    // Handle exceptions, when added, then send confirmation or error
                    output.sendInfoMessage("Moved " + clientId + " to Room " + join.getRoomId());
                    break;
                default:
                    logger.error("invalid command packet received: {}", command.toString());
                    break;
            }
        } catch (IOException err) {
            logger.error("error while sending to client: ", err);
        }
    }

    private void handleMessage(Message message, ProtocolOutputStream output) {
        switch (message.getMessageType()) {
            case MessageType.Text:
                var text = (TextMessage) message;
                var room = state.getClientRoom(socket);
                // room.sendPacket(sender, text);

                logger.info("someone sent a message to Room {}: {}", room.getId(), text.getText());

                // Handle exceptions, when added, then send confirmation or error
                break;
            default:
                logger.error("invalid message packet received: {}", message.toString());
                break;
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
