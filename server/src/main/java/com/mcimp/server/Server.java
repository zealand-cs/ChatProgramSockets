package com.mcimp.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.mcimp.server.utils.EmojiReplacer;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);

    private final ExecutorService pool;
    private final int port;
    private final ServerState state;

    public Server(int threads, int port) {
        pool = Executors.newFixedThreadPool(threads);
        this.port = port;
        this.state = new ServerState();
    }

    public void startServer() {
        // Makes sure to clean up, when the server stops unexpectedly
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                state.close();
            } catch (IOException err) {
                err.printStackTrace();
            }
        }));

        EmojiReplacer replacer = new EmojiReplacer("emojiLookup.csv");

        // Try with resources automatically closes the ServerSocket if an exception
        // occurs
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server is listening on port " + port);

            // Necessary to handle multiple clients at the same time
            while (true) {
                Socket clientSocket = serverSocket.accept();

                var client = new ClientHandler(clientSocket, state, replacer);

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
}
