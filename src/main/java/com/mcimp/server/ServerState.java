package com.mcimp.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mcimp.protocol.Packet;
import com.mcimp.protocol.commands.JoinCommand;
import com.mcimp.protocol.messages.TextMessage;

public class ServerState {
    private static final Logger logger = LogManager.getLogger(ServerState.class);

    private short latestClientId = 0;
    private Map<Socket, Short> clientIds;
    private Map<Short, ClientHandler> clients;

    private Map<String, Room> rooms;

    // Mapping of client ids to room ids
    private Map<Short, Room> roomClients;
    private Room defaultRoom;

    public ServerState(Map<InetAddress, ClientHandler> clients) {
        this.clientIds = Collections.synchronizedMap(new HashMap<>());
        this.clients = Collections.synchronizedMap(new HashMap<>());

        this.rooms = Collections.synchronizedMap(new HashMap<>());
        this.roomClients = Collections.synchronizedMap(new HashMap<>());

        this.defaultRoom = createRoom(JoinCommand.DEFAULT_ROOM, "Global");
    }

    public synchronized void addClient(Socket socket, ClientHandler client) {
        clientIds.put(socket, ++latestClientId);
        clients.put(latestClientId, client);
        roomClients.put(latestClientId, defaultRoom);
        defaultRoom.addClient(client);
    }

    public synchronized void removeClient(Socket socket) {
        var id = clientIds.remove(socket);
        var client = clients.remove(id);
        var room = getClientRoom(id);
        room.removeClient(client);
        roomClients.remove(id);
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
        return getClientRoom(getClientId(socket));
    }

    public Room getClientRoom(Short clientId) {
        var room = roomClients.get(clientId);
        // TODO: Handle null case
        return room;
    }

    public synchronized void moveClientToRoom(Socket socket, String roomId) {
        moveClientToRoom(getClientId(socket), roomId);
    }

    public synchronized void moveClientToRoom(Short clientId, String roomId) {
        var client = clients.get(clientId);
        // TODO: Handle null case

        var newRoom = rooms.get(roomId);
        // TODO: Handle null case

        var oldRoom = roomClients.put(clientId, newRoom);
        if (oldRoom != null) {
            oldRoom.removeClient(client);
        }

        newRoom.addClient(client);
    }

    public ClientHandler getClient(Socket socket) {
        var id = clientIds.get(socket);
        return getClient(id);
    }

    public ClientHandler getClient(short id) {
        return clients.get(id);
    }

    public Short getClientId(Socket socket) {
        return clientIds.get(socket);
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
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
