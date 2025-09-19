package com.mcimp.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mcimp.protocol.Packet;
import com.mcimp.protocol.messages.SystemMessage;
import com.mcimp.protocol.messages.TextMessage;
import com.mcimp.utils.EmojiReplacer;

class Room {
    private String id;

    private List<ClientHandler> clients;

    private final EmojiReplacer replacer;

    public Room(String id, EmojiReplacer replacer) {
        this.id = id;
        this.clients = new ArrayList<>();

        this.replacer = replacer;
    }

    public void broadcastAll(Packet packet) throws IOException {
        for (var client : clients) {
            client.getOutputStream().send(packet);
        }
    }

    public void broadcast(ClientHandler sender, Packet packet) throws IOException {
        var text = (TextMessage) packet;
        var replaced = replacer.replaceEmojis(text.getText());

        for (var client : clients) {
            if (client == sender) {
                continue;
            }

            client.getOutputStream()
                    .send(SystemMessage.pure("[" + getId() + "] " + sender.getUsername() + ": " + replaced));
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

    public boolean isEmpty() {
        return clients.isEmpty();
    }
}
