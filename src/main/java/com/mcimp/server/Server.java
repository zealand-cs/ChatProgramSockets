package com.mcimp.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mcimp.utils.EmojiReplace;

public class Server {

    private final ExecutorService pool;

    public Server(int threads) {
        pool = Executors.newFixedThreadPool(threads);
    }

    private volatile boolean running = false;

    public void stop() {
        running = false;
    }

    public void startServer() {
        /*String emojiLookupPath = this.getClass().getResource("/emojiLookup.csv").getPath();
        EmojiReplace replacer = new EmojiReplace("emojiLookup.csv");*/

        // Try with resources automatically closes the ServerSocket if an exception
        // occurs
        try (ServerSocket serverSocket = new ServerSocket(5555)) {
            System.out.println("Server is listening on port 5555");

            // Necessary to handle multiple clients at the same time
            while (running) {
                Socket clientSocket = serverSocket.accept();
                /*
                 * Creates a new thread to handle clients seperately
                 * ClientHandler implements Runnable
                 */
                new Thread(new ClientHandler(clientSocket, 5 * 60 * 1000)).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(5);
        server.startServer();
    }

}
