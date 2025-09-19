package com.mcimp.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mcimp.protocol.client.ClientOutputStream;
import com.mcimp.protocol.client.packets.ConnectPacket;
import com.mcimp.protocol.server.ServerInputStream;

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
                    var input = new ServerInputStream(socket.getInputStream());
                    var output = new ClientOutputStream(socket.getOutputStream());) {

                output.send(new ConnectPacket());

                // Start multiple threads, waiting for
                var tasks = new ArrayList<Callable<Object>>(2);

                // Handle incoming packets
                var incomingHandler = Executors.callable(new IncomingHandler(input, terminal));
                tasks.add(incomingHandler);

                var outgoingHandler = Executors.callable(new OutgoingHandler(output, terminal));
                tasks.add(outgoingHandler);

                pool.invokeAny(tasks);
            } catch (ExecutionException ex) {
                logger.info("closing everything down: ", ex);
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

        } catch (IOException e) {
            logger.error("unknown IO Exception occoured: " + e);
        } finally {
            try {
                terminal.close();
                pool.close();
            } catch (IOException ex) {
                throw new RuntimeException("exception while closing applicaiton: " + ex);
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 5555);
        client.start();
    }
}
