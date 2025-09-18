package com.mcimp.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mcimp.protocol.Packet;
import com.mcimp.protocol.commands.JoinCommand;
import com.mcimp.protocol.messages.TextMessage;
import com.mcimp.repository.UserRepository;
import com.mcimp.utils.BiMap;
import com.mcimp.utils.HashBiMap;
import com.mcimp.utils.SynchronizedBiMap;

public class ServerState {
    private Map<Socket, ClientHandler> clients;

    private BiMap<Socket, String> authenticatedUsers;
    private UserRepository userRepository;

    private Room defaultRoom;
    private Map<String, Room> rooms;
    private Map<Socket, Room> roomClients;

    public ServerState(UserRepository userRepository) {
        this.clients = Collections.synchronizedMap(new HashMap<>());

        this.authenticatedUsers = new SynchronizedBiMap<>(new HashBiMap<>());
        this.userRepository = userRepository;

        this.rooms = Collections.synchronizedMap(new HashMap<>());
        this.roomClients = Collections.synchronizedMap(new HashMap<>());
        this.defaultRoom = createRoom(JoinCommand.DEFAULT_ROOM, "Global");
    }

    public synchronized void addClient(Socket socket, ClientHandler client) {
        clients.put(socket, client);
        roomClients.put(socket, defaultRoom);
        defaultRoom.addClient(client);
    }

    public synchronized void removeClient(Socket socket) {
        var client = clients.remove(socket);
        var room = getClientRoom(socket);
        room.removeClient(client);
        roomClients.remove(socket);
    }

    public synchronized boolean authenticate(String username, String password) {
        return userRepository.authenticate(username, password);
    }

    public synchronized boolean userExists(String username) {
        return userRepository.userExists(username);
    }

    public synchronized void addAuthSession(String username, String password) throws IOException {
        userRepository.addUser(username, password);
    }

    public synchronized boolean isAuthenticated(Socket socket) {
        return authenticatedUsers.get(socket) != null;
    }

    public synchronized void loginUser(Socket socket, String username) {
        authenticatedUsers.put(socket, username);
    }

    public synchronized Room createRoom(String id, String displayName) {
        var room = new Room(id, displayName);
        rooms.put(id, room);
        return room;
    }

    public synchronized void removeRoom(String id) {
        rooms.remove(id);
    }

    public Room getClientRoom(Socket socket) {
        var room = roomClients.get(socket);
        return room;
    }

    public synchronized void moveClientToRoom(Socket socket, Room newRoom) {
        var client = getClient(socket);

        var oldRoom = roomClients.put(socket, newRoom);
        if (oldRoom != null) {
            oldRoom.removeClient(client);
        }

        newRoom.addClient(client);
    }

    public ClientHandler getClient(Socket socket) {
        var client = clients.get(socket);
        if (client == null) {
            // Crashing since this is an unrecoverable state.
            // It should not even be possible in the first place.
            throw new RuntimeException("socket without corresponding client");
        }
        return client;
    }

    public Optional<Room> getRoom(String roomId) {
        return Optional.ofNullable(rooms.get(roomId));
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }
}

class Room {
    private String id;
    private String name;

    private List<ClientHandler> clients;

    public Room(String id, String name) {
        this.id = id;
        this.name = name;
        this.clients = new ArrayList<>();
    }

    public void broadcastAll(Packet packet) throws IOException {
        for (var client : clients) {
            client.getOutputStream().writePacket(packet);
        }
    }

    public void broadcast(ClientHandler sender, Packet packet) throws IOException {
        var text = (TextMessage) packet;
        for (var client : clients) {
            if (client == sender) {
                continue;
            }

            client.getOutputStream().sendInfoMessage(sender.getUsername() + ": " + text.getText());
        }
    }

    public void addClient(ClientHandler clien) {
        clients.add(clien);
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
