package com.mcimp.server;

import java.io.*;
import java.net.Socket;
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
        try {
            socket.setSoTimeout(timeout);

            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));


            while (true) {
                writer.println("Welcome to the chat server!");
                writer.println("Enter your username:");
                this.username = reader.readLine();
                if (username == null) {
                    return;
                }

                if (repo.userExists(username)) {
                    // login
                    writer.println("User exists. Enter your password:");
                    String password = reader.readLine();

                    if (!repo.authenticate(username, password)) {
                        writer.println("Invalid password. Connection closed.");
                        socket.close();
                        return;
                    }
                } else {
                    // registration
                    writer.println("No account found. Enter a password to register:");
                    String password = reader.readLine();
                    repo.addUser(username, password);
                    writer.println("Account created successfully!");

                }

                if (!loggedInUsers.add(username)) {
                    writer.println("User already logged in from another session.");
                    socket.close();
                    continue;
                }

                writer.println("LOGIN_SUCCESS");
                System.out.println(username + " logged in.");
                break;
            }

        } catch (IOException e) {
            logger.error(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
    
    public void sendMessage(Room room, ClientHandler sender, String message) {
        
    }
}
