package com.mcimp.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mcimp.protocol.server.ServerPacket;

class Room {
    private String id;

    private List<ClientHandler> clients;

    public Room(String id) {
        this.id = id;
        this.clients = new ArrayList<>();
    }

    public void broadcast(ServerPacket packet) throws IOException {
        broadcast(packet, new ClientHandler[0]);
    }

    public void broadcast(ServerPacket packet, ClientHandler exclude) throws IOException {
        broadcast(packet, new ClientHandler[] { exclude });
    }

    public void broadcast(ServerPacket packet, ClientHandler[] exclude) throws IOException {
        var excludedList = new ArrayList<>(clients);
        excludedList.removeAll(Arrays.asList(exclude));

        for (var client : excludedList) {
            client.getOutputStream().send(packet);
        }
    }

    public void addClient(ClientHandler client) {
        clients.add(client);
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public String getId() {
        return id;
    }

    public boolean isEmpty() {
        return clients.isEmpty();
    }
}
