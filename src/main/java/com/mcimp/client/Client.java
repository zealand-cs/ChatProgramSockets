package com.mcimp.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
            socket.setSoTimeout(timeout);

            // Auto-flush enabled even though it's probably not necessary
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

            while (true) {
                Thread.sleep(5000);

                writer.flush();
            }
        } catch (SocketTimeoutException e) {
            logger.error("socket connection to " + hostname + ":" + port + " timed out: " + e);
        } catch (IOException e) {
            logger.error("unknown IO Exception occoured: " + e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 2244);
        client.start();
    }
}
