package com.mcimp.server;

import java.io.*;
import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    private final int timeout;

    private Socket socket;

    private String username;

    public ClientHandler(Socket socket, int timeout) {
        this.socket = socket;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(timeout);

            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

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
