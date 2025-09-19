package com.mcimp.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.mcimp.repository.UserRepository;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);

    private final ExecutorService pool;
    private int port;

    private UserRepository repo;
    private ServerState state;

    public Server(int threads, int port) {
        pool = Executors.newFixedThreadPool(threads);
        this.port = port;

        try {
            this.repo = new UserRepository("users.json");
        } catch (IOException ex) {
            throw new RuntimeException("users.json could not be read");
        }

        this.state = new ServerState(repo);
    }

    private volatile boolean running = true;

    public void stop() {
        running = false;
    }

    public void startServer() {
        running = true;
        // String emojiLookupPath =
        // this.getClass().getResource("emojiLookup.csv").getPath();
        // EmojiReplace replacer = new EmojiReplace(emojiLookupPath);

        // Try with resources automatically closes the ServerSocket if an exception
        // occurs
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server is listening on port " + port);

            // Necessary to handle multiple clients at the same time
            while (running) {
                Socket clientSocket = serverSocket.accept();

                var client = new ClientHandler(clientSocket, state);

                state.addClient(clientSocket, client);

                /*
                 * Creates a new thread to handle clients seperately
                 * ClientHandler implements Runnable
                 */
                pool.execute(client);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(5, 5555);
        server.startServer();
    }
}
