package com.mcimp.server;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerState {
    private short latestId = 0;
    private Map<InetAddress, Short> clientIds;
    private Map<Short, ClientHandler> clients;

    private List<Room> rooms;

    public ServerState(Map<InetAddress, ClientHandler> clients) {
        this.clientIds = Collections.synchronizedMap(new HashMap<>());
        this.clients = Collections.synchronizedMap(new HashMap<>());
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

    public ClientHandler getClient(short id) {
        return clients.get(id);
    }

    public ClientHandler getClient(InetAddress address) {
        var id = clientIds.get(address);
        return clients.get(id);
    }
}

class Room {
    private int id;
    private String name;

    private List<ClientHandler> clients;

    public void sendMessage(ClientHandler sender, String message) {
        for (var client : clients) {
            client.sendMessage(this, sender, message);
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
