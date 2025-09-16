package com.mcimp.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mcimp.protocol.ProtocolInputStream;
import com.mcimp.protocol.ProtocolOutputStream;
import com.mcimp.protocol.messages.SystemMessage;
import com.mcimp.protocol.packets.AuthPacket;
import com.mcimp.protocol.packets.AuthType;
import com.mcimp.protocol.packets.ConnectPacket;

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

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            try (
                    ProtocolInputStream input = new ProtocolInputStream(socket.getInputStream());
                    ProtocolOutputStream output = new ProtocolOutputStream(socket.getOutputStream())) {

                output.writePacket(new ConnectPacket());

                var welcomeMessage = (SystemMessage) input.readPacket();
                System.out.println(welcomeMessage.getText());

                System.out.println("Login or register?");
                System.out.println("1. login");
                System.out.println("2. register");

                var authType = consoleReader.readLine();

                output.writePacket(new AuthPacket(AuthType.Login, "t", "t"));

                // Handle incoming packets
                new Thread(new IncomingHandler(input)).start();
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
