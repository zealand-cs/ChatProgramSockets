package com.mcimp.server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(5, 5555);
        server.startServer();
    }
}
