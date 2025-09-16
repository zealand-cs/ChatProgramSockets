package com.mcimp.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.mcimp.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);


    private static UserRepository repo;
    private Set<String> loggedInUsers = ConcurrentHashMap.newKeySet();
    private final int timeout;
    private Socket socket;

    private String username;

    public ClientHandler(Socket socket, int timeout, UserRepository repo, Set<String> loggedInUsers) {
        this.socket = socket;
        this.timeout = timeout;
        this.repo = repo;
        this.loggedInUsers = loggedInUsers;
    }

    @Override
    public void run() {
        try (Socket socket = this.socket;

            PrintWriter writer = new PrintWriter
                (new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

                    socket.setSoTimeout(timeout);

                    if (handleLoginOrRegistration(writer, reader)) {
                        writer.println("LOGIN SUCCESSFUL");
                        logger.info("User {} logged in", username);
                    }


                } catch (IOException e) {
                    logger.error("Client error: ", e);
                }
        }

    public Socket getSocket() {
        return socket;
    }

    public void sendMessage(Room room, ClientHandler sender, String message) {

    }

    private boolean handleLoginOrRegistration(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("Welcome to the chat server!");
        writer.println("Enter your username");
        username = reader.readLine();
        if (username == null) {
            return false;
        }

        if (repo.userExists(username)) {
            return handleLogin(writer, reader);
        } else {
            return handleRegistration(writer, reader);
        }
    }

    private boolean handleLogin(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("User exists. Enter password: ");
        String password = reader.readLine();
        if (!repo.authenticate(username, password)) {
            writer.println("Invalid password, Connection closed");
            socket.close();
            return false;
        }
        return registerSession(writer);
    }

    private boolean handleRegistration(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("No account found. Enter a password to register:");
        String password = reader.readLine();
        repo.addUser(username, password);
        writer.println("Account created successfully");
        return registerSession(writer);
    }

    private boolean registerSession(PrintWriter writer) throws IOException {
        if (!loggedInUsers.add(username)) {
            writer.println("User already logged in from another session");
            socket.close();
            return false;
        }
        return true;
    }


}
