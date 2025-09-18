package com.mcimp.server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mcimp.protocol.Packet;
import com.mcimp.protocol.commands.JoinCommand;

public class ServerState {
    private short latestClientId = 0;
    private Map<InetAddress, Short> clientIds;
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

    public synchronized void addClient(InetAddress address, ClientHandler client) {
        clientIds.put(address, ++latestClientId);
        clients.put(latestClientId, client);
        roomClients.put(latestClientId, JoinCommand.DEFAULT_ROOM);
    }

    public synchronized void removeClient(InetAddress address) {
        var id = clientIds.get(address);
        clients.remove(id);
        roomClients.remove(id);
        clientIds.remove(address);
    }

    public synchronized void createRoom(String id, String displayName) {
        rooms.put(id, new Room(id, displayName));
    }

    public synchronized void removeRoom(String id) {
        rooms.remove(id);
    }

    public synchronized void moveClientToRoom(Short clientId, String roomId) {
        var client = clients.get(clientId);
        // TODO: Handle null case

        var newRoom = rooms.get(roomId);
        // TODO: Handle null case

        var oldRoomId = roomClients.put(clientId, roomId);
        var room = rooms.get(oldRoomId);
        // TODO: Handle null case
        room.removeClient(client);
        newRoom.addClient(client);
    }

    public Room getClientRoom(InetAddress address) {
        var clientId = getClientId(address);
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

    public ClientHandler getClient(short id) {
        return clients.get(id);
    }

    public ClientHandler getClient(InetAddress address) {
        var id = clientIds.get(address);
        return clients.get(id);
    }

    public Short getClientId(InetAddress address) {
        return clientIds.get(address);
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
