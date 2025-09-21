package com.mcimp.server.repository;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.*;

public class UserRepository {
    private final File file;
    private final Map<String, String> users = new HashMap<>();

    public UserRepository(String file) throws IOException {
        this.file = new File(file);
        if (this.file.exists()) {
            load();
        } else {
            save();
        }
    }

    public synchronized boolean authenticate(String username, String password) {
        String storedHash = users.get(username);
        return storedHash != null && storedHash.equals(PasswordHasher.hash(password));
    }

    public synchronized boolean addUser(String username, String password) throws IOException {
        if (users.containsKey(username)) return false;
        users.put(username, PasswordHasher.hash(password));
        save();
        return true;
    }

    private void load() throws IOException {
        try (Reader reader = new FileReader(file)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray array = root.getAsJsonArray("users");
            if (array != null) {
                for (JsonElement element : array) {
                    JsonObject object = element.getAsJsonObject();
                    users.put(object.get("username").getAsString(), object.get("password").getAsString());
                }
            }
        }
    }

    private void save() throws IOException {
        JsonObject root = new JsonObject();
        JsonArray array = new JsonArray();
        for (Map.Entry<String, String> entry : users.entrySet()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("username", entry.getKey());
            obj.addProperty("password", entry.getValue());
            array.add(obj);
        }
        root.add("users", array);

        try (Writer writer = new FileWriter(file)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(root, writer);
        }
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

}
