package com.mcimp.server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mcimp.protocol.Packet;

public class ServerState {
    private short latestId = 0;
    private Map<InetAddress, Short> clientIds;
    private Map<Short, ClientHandler> clients;

    private short latestRoomId = 0;
    private List<Room> rooms;

    public ServerState(Map<InetAddress, ClientHandler> clients) {
        this.clientIds = Collections.synchronizedMap(new HashMap<>());
        this.clients = Collections.synchronizedMap(new HashMap<>());

        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(latestRoomId, "Global"));
        this.rooms = Collections.synchronizedList(rooms);
    }

    public void addClient(InetAddress address, ClientHandler client) {
        synchronized (clients) {
            clientIds.put(address, ++latestId);
            clients.put(latestId, client);
        }
    }

    public void removeClient(InetAddress address) {
        synchronized (clients) {
            var id = clientIds.get(address);
            clients.remove(id);
            clientIds.remove(address);
        }
    }

    public void createRoom(String name) {
        rooms.add(new Room(++latestRoomId, name));
    }

    public void removeRoom(Room room) {
        rooms.remove(room);
    }

    public ClientHandler getClient(short id) {
        return clients.get(id);
    }

    public ClientHandler getClient(InetAddress address) {
        var id = clientIds.get(address);
        return clients.get(id);
    }

    public List<Room> getRooms() {
        return rooms;
    }
}

class Room {
    private int id;
    private String name;

    private List<ClientHandler> clients;

    public Room(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void sendPacket(ClientHandler sender, Packet packet) {
        for (var client : clients) {
            // client.sendPacket(sender, packet);
        }
    }

    public void addClient(ClientHandler client) {
        clients.add(client);
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
