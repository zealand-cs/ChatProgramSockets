package com.mcimp.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Client {
    private static final Logger logger = LogManager.getLogger(Client.class);

    private String hostname;
    private int port;
    private int timeout;

    public String name;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void start() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(hostname, port), timeout);


            // Auto-flush enabled even though it's probably not necessary
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));


            new Thread(() -> {
                try {
                    String serverMsg;
                    while ((serverMsg = reader.readLine()) != null) {
                        System.out.println("SERVER: " + serverMsg);
                    }
                } catch (IOException e) {
                    logger.error("error while reading from server: " + e);
                }
        }).start();

            String input;
            while ((input = consoleReader.readLine()) != null) {
                writer.println(input);
            }

        } catch (SocketTimeoutException e) {
            logger.error("socket connection to " + hostname + ":" + port + " timed out: " + e);
        } catch (IOException e) {
            logger.error("unknown IO Exception occoured: " + e);
        }
    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 5555);
        client.start();
    }
}
