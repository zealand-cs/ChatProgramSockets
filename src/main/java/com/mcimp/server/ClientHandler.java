package com.mcimp.server;

import java.io.*;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.mcimp.protocol.Packet;
import com.mcimp.protocol.PacketType;
import com.mcimp.protocol.ProtocolInputStream;
import com.mcimp.protocol.ProtocolOutputStream;
import com.mcimp.protocol.messages.SystemMessage;
import com.mcimp.protocol.packets.AuthPacket;
import com.mcimp.protocol.packets.AuthType;
import com.mcimp.repository.UserRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    private Set<String> loggedInUsers = ConcurrentHashMap.newKeySet();
    private final int timeout;
    private Socket socket;
    private ServerState state;
    private UserRepository repo;

    private String username;

    public ClientHandler(Socket socket, int timeout, UserRepository repo, Set<String> loggedInUsers,
            ServerState state) {
        this.socket = socket;
        this.timeout = timeout;
        this.repo = repo;
        this.loggedInUsers = loggedInUsers;
        this.state = state;
    }

    @Override
    public void run() {
        try (
                Socket socket = this.socket;
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

            output.writePacket(new SystemMessage("info", "Welcome to the server!"));

            var authPacket = (AuthPacket) input.readPacket();
            assert authPacket.getType() == PacketType.Auth;

            if (authPacket.getAuthType() == AuthType.Login) {
                if (!repo.userExists(authPacket.getUsername())) {
                    logger.warn(authPacket.getUsername() + " doesn't exist");
                    socket.close();
                }

                var authenticated = repo.authenticate(authPacket.getUsername(), authPacket.getPassword());

                if (!authenticated) {
                    logger.warn(authPacket.getUsername() + " logged in with wrong password");
                    socket.close();
                }
            } else {
                if (repo.userExists(authPacket.getUsername())) {
                    logger.warn(authPacket.getUsername() + " tried to register with existing user");
                    socket.close();
                }

                repo.addUser(authPacket.getUsername(), authPacket.getPassword());
            }

            username = authPacket.getUsername();
            loggedInUsers.add(authPacket.getUsername());
            logger.info("{} logged in successfully", authPacket.getUsername());

            var builder = new StringBuilder("To begin chatting with someone, join a room!");
            for (var room : state.getRooms()) {
                builder.append(room.getId() + " - " + room.getName());
            }

            output.writePacket(new SystemMessage("info", builder.toString()));

        } catch (IOException e) {
            logger.error("Client error: ", e);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void sendPacket(Packet packet, DataOutputStream stream) {
        try {
            packet.writeToStream(stream);
        } catch (IOException e) {
            logger.error("Something went wrong when sending packet to client: " + e);
        }
    }
}
