package com.mcimp.server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mcimp.utils.EmojiReplace;

public class Server {

    private final ExecutorService pool;

    private int port;
    private int clientTimeout;

    private ServerState state;

    public Server(int threads, int port, int timeout) {
        pool = Executors.newFixedThreadPool(threads);

        this.port = port;
        this.clientTimeout = timeout;
        this.state = new ServerState(new HashMap<>());
    }

    private volatile boolean running = false;

    public void stop() {
        running = false;
    }

    public void startServer() {
        String emojiLookupPath = this.getClass().getResource("emojiLookup.csv").getPath();
        EmojiReplace replacer = new EmojiReplace(emojiLookupPath);

        // Try with resources automatically closes the ServerSocket if an exception
        // occurs
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            // Necessary to handle multiple clients at the same time
            while (running) {
                Socket clientSocket = serverSocket.accept();

                var client = new ClientHandler(clientSocket, clientTimeout);
                var address = clientSocket.getInetAddress();

                state.addClient(address, client);

                /*
                 * Creates a new thread to handle clients seperately
                 * ClientHandler implements Runnable
                 */
                pool.execute(new ClientHandler(clientSocket, clientTimeout));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(5, 5555, 5 * 60 * 1000);
        server.startServer();
    }
}
