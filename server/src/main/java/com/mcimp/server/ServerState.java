package com.mcimp.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mcimp.server.repository.TmpFileRepository;
import com.mcimp.server.repository.UserRepository;
import com.mcimp.server.utils.BiMap;
import com.mcimp.server.utils.HashBiMap;
import com.mcimp.server.utils.SynchronizedBiMap;

public class ServerState implements AutoCloseable {
    public static final String DEFAULT_ROOM = "global";

    private final Map<Socket, ClientHandler> clients;

    private final BiMap<Socket, String> authenticatedUsers;

    private final UserRepository userRepository;
    private final TmpFileRepository fileRepository;

    private final Room defaultRoom;
    private final Map<String, Room> rooms;
    private final Map<Socket, Room> roomClients;

    public ServerState() {
        this.clients = Collections.synchronizedMap(new HashMap<>());

        this.authenticatedUsers = new SynchronizedBiMap<>(new HashBiMap<>());

        try {
            this.userRepository = new UserRepository("users.json");
            this.fileRepository = new TmpFileRepository("./uploads");
        } catch (IOException ex) {
            throw new RuntimeException("users.json could not be read");
        }

        this.rooms = Collections.synchronizedMap(new HashMap<>());
        this.roomClients = Collections.synchronizedMap(new HashMap<>());
        this.defaultRoom = createRoom(DEFAULT_ROOM);
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
        authenticatedUsers.remove(socket);
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

    public synchronized Room createRoom(String id) {
        var room = new Room(id);
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

            // If room is empty and NOT the default room, delete it.
            if (oldRoom.isEmpty() && !oldRoom.getId().equals(DEFAULT_ROOM)) {
                removeRoom(oldRoom.getId());
            }
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

    public Collection<ClientHandler> getClients() {
        return clients.values();
    }

    public Collection<String> getAuthenticatedUsers() {
        return authenticatedUsers.valueSet();
    }

    public Optional<Room> getRoom(String roomId) {
        return Optional.ofNullable(rooms.get(roomId));
    }

    public Collection<Room> getRooms() {
        return rooms.values();
    }

    public TmpFileRepository getFileRepository() {
        return fileRepository;
    }

    @Override
    public void close() throws IOException {
        fileRepository.close();
        for (var socket : clients.keySet()) {
            socket.close();
        }
    }
}
