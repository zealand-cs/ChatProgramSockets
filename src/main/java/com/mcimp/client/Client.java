package com.mcimp.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mcimp.protocol.ProtocolInputStream;
import com.mcimp.protocol.ProtocolOutputStream;
import com.mcimp.protocol.messages.SystemMessage;
import com.mcimp.protocol.packets.ConnectPacket;

public class Client {
    private static final Logger logger = LogManager.getLogger(Client.class);
    private final ExecutorService pool = Executors.newFixedThreadPool(2);

    private String hostname;
    private int port;
    private int timeout;

    public String name;

    private ClientTerminal terminal;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.terminal = new ClientTerminal();
    }

    public void start() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(hostname, port), timeout);

            try (
                    ProtocolInputStream input = new ProtocolInputStream(socket.getInputStream());
                    ProtocolOutputStream output = new ProtocolOutputStream(socket.getOutputStream());) {

                output.writePacket(new ConnectPacket());

                var welcomeMessage = (SystemMessage) input.readPacket();
                terminal.write(welcomeMessage.getText() + "\n");
                terminal.flush();

                // Start multiple threads, waiting for
                var tasks = new ArrayList<Callable<Object>>(2);

                // Handle incoming packets
                var incomingHandler = Executors.callable(new IncomingHandler(input, terminal));
                tasks.add(incomingHandler);

                var outgoingHandler = Executors.callable(new OutgoingHandler(output, terminal));
                tasks.add(outgoingHandler);

                pool.invokeAll(tasks);
                // TODO: Handle exceptions, when added, then send confirmation or error
            } catch (InterruptedException e) {
                logger.error("error occoured in pool: ", e);
            } finally {
                // Wait for shutdown of pool and handle it acordingly.
                // See
                // https://www.baeldung.com/java-executor-wait-for-threads#after-executors-shutdown
                pool.shutdown();

                try {
                    if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                        pool.shutdownNow();
                    }
                } catch (InterruptedException ex) {
                    pool.shutdownNow();
                    Thread.currentThread().interrupt();
                }
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
