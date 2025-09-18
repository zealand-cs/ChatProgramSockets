package com.mcimp.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mcimp.protocol.Packet;
import com.mcimp.protocol.commands.JoinCommand;

class SocketIdentifier {
    private InetAddress address;
    private int port;

    public SocketIdentifier(Socket socket) {
        this.address = socket.getInetAddress();
        this.port = socket.getPort();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final var other = (SocketIdentifier) obj;

        if (!address.equals(other.address) || port != other.port) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + address.hashCode();
        hash = 53 * hash + port;
        return hash;
    }
}

public class ServerState {
    private static final Logger logger = LogManager.getLogger(ServerState.class);

    private short latestClientId = 0;
    private Map<SocketIdentifier, Short> clientIds;
    private Map<Short, ClientHandler> clients;

    private Map<String, Room> rooms;

    // Mapping of client ids to room ids
    private Map<Short, String> roomClients;

    public ServerState(Map<InetAddress, ClientHandler> clients) {
        this.clientIds = Collections.synchronizedMap(new HashMap<>());
        this.clients = Collections.synchronizedMap(new HashMap<>());

        this.rooms = Collections.synchronizedMap(new HashMap<>());
        this.roomClients = Collections.synchronizedMap(new HashMap<>());

        createRoom(JoinCommand.DEFAULT_ROOM, "Global");
    }

    public synchronized void addClient(Socket socket, ClientHandler client) {
        var identifier = new SocketIdentifier(socket);
        addClient(identifier, client);
    }

    public synchronized void addClient(SocketIdentifier identifier, ClientHandler client) {
        clientIds.put(identifier, ++latestClientId);
        clients.put(latestClientId, client);
        roomClients.put(latestClientId, JoinCommand.DEFAULT_ROOM);
    }

    public synchronized void removeClient(Socket socket) {
        var identifier = new SocketIdentifier(socket);
        removeClient(identifier);
    }

    public synchronized void removeClient(SocketIdentifier identifier) {
        var id = clientIds.get(identifier);
        clients.remove(id);
        roomClients.remove(id);
        clientIds.remove(identifier);
    }

    public synchronized void createRoom(String id, String displayName) {
        rooms.put(id, new Room(id, displayName));
    }

    public synchronized void removeRoom(String id) {
        rooms.remove(id);
    }

    public Room getClientRoom(Socket socket) {
        var identifier = new SocketIdentifier(socket);
        return getClientRoom(identifier);
    }

    public Room getClientRoom(SocketIdentifier identifier) {
        var clientId = getClientId(identifier);
        // TODO: Handle null case
        return getClientRoom(clientId);
    }

    public Room getClientRoom(Short clientId) {
        var roomId = roomClients.get(clientId);
        // TODO: Handle null case
        var room = rooms.get(roomId);
        // TODO: Handle null case
        return room;
    }

    public synchronized void moveClientToRoom(Short clientId, String roomId) {
        var client = clients.get(clientId);
        // TODO: Handle null case

        var newRoom = rooms.get(roomId);
        // TODO: Handle null case

        var oldRoomId = roomClients.put(clientId, roomId);
        var room = rooms.get(oldRoomId);
        if (room != null) {
            room.removeClient(client);
        }

        newRoom.addClient(client);
    }

    public ClientHandler getClient(Socket socket) {
        var identifier = new SocketIdentifier(socket);
        return getClient(identifier);
    }

    public ClientHandler getClient(SocketIdentifier identifier) {
        var id = clientIds.get(identifier);
        return getClient(id);
    }

    public ClientHandler getClient(short id) {
        return clients.get(id);
    }

    public Short getClientId(Socket socket) {
        var identifier = new SocketIdentifier(socket);
        return getClientId(identifier);
    }

    public Short getClientId(SocketIdentifier identifier) {
        return clientIds.get(identifier);
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

    public void sendPacket(ClientHandler sender, Packet packet) throws IOException {
        for (var client : clients) {
            // client.getOutput().writePacket(packet);
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
