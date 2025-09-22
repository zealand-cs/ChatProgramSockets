package com.mcimp.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mcimp.protocol.client.ClientOutputStream;
import com.mcimp.protocol.client.ClientPacketId;
import com.mcimp.protocol.client.packets.UnitPacket;
import com.mcimp.protocol.server.ServerInputStream;

public class Client {
    private static final Logger logger = LogManager.getLogger(Client.class);
    private final ExecutorService pool = Executors.newFixedThreadPool(2);

    private static final int DEFAULT_PORT = 5555;

    private int timeout;

    public String name;

    private ClientTerminal terminal;

    public Client() {
        this.terminal = new ClientTerminal();
    }

    public void start() {
        terminal.writeln("Connect to a remote server or type `.` to connect locally");
        terminal.flush();

        var connectionString = terminal.readLine("host > ");

        String hostname = "127.0.0.1";
        int port = DEFAULT_PORT;
        if (!connectionString.equals(".")) {
            var splitted = connectionString.split(":", 2);
            hostname = splitted[0];
            if (splitted.length > 1 && splitted[1] != null) {
                port = Integer.parseInt(splitted[1]);
            }
        }

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(hostname, port), timeout);

            try (
                    var input = new ServerInputStream(socket.getInputStream());
                    var output = new ClientOutputStream(socket.getOutputStream());) {

                output.send(new UnitPacket(ClientPacketId.Connect));

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
        } catch (UnknownHostException ex) {
            logger.error("The provided host wasn't valid: " + ex.getMessage());
        } catch (IOException ex) {
            logger.error("unknown IO Exception occoured: ", ex);
        } finally {
            try {
                terminal.close();
                pool.close();
            } catch (IOException ex) {
                throw new RuntimeException("exception while closing applicaiton: " + ex);
            }
        }
    }
}
