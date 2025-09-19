package com.mcimp.client;

import java.io.EOFException;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.apache.logging.log4j.LogManager;

import com.mcimp.protocol.server.ServerInputStream;
import com.mcimp.protocol.server.ServerPacketId;
import com.mcimp.protocol.server.packets.SystemMessagePacket;
import com.mcimp.protocol.server.packets.UserMessagePacket;
import com.mcimp.protocol.server.packets.SystemMessageLevel;

public class IncomingHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(IncomingHandler.class);

    private final ServerInputStream stream;
    private final ClientTerminal terminal;

    public IncomingHandler(ServerInputStream stream, ClientTerminal terminal) {
        this.stream = stream;
        this.terminal = terminal;
    }

    @Override
    public void run() {
        // Outer try catch to stop if the server stops before the client
        // When the same catch is in the loop, it will keep on error'ing,
        // very quickly indeed.
        // Ooohhhh how we loooove nesting...

        try {
            while (true) {
                var packet = stream.read();

                switch (packet.getType()) {
                    case ServerPacketId.Connected:
                        logger.info("Server: " + packet);
                        // TODO: Correct output
                        break;
                    case ServerPacketId.Disconnected:
                        logger.info("Server: " + packet);
                        // TODO: Correct output
                        break;
                    case ServerPacketId.SystemMessage:
                        handleSystemMessage((SystemMessagePacket) packet);
                        break;
                    case ServerPacketId.UserMessage:
                        handleUserMessage((UserMessagePacket) packet);
                        break;
                    default:
                        logger.warn("unhandled packet: ", packet.toString());
                }
            }
        } catch (EOFException ex) {
            // This EOF happens when there's no packets to read.
            // Nothing is done to handle it, except continuing
            // to make the thread pool close.
        } catch (IOException e) {
            // Empty catch to return and close everything in client class correctly
        }
    }

    private void handleSystemMessage(SystemMessagePacket message) {
        String msg = formatSystemMessage(message);
        terminal.write(msg + "\n");
        terminal.flush();
    }

    private String formatSystemMessage(SystemMessagePacket packet) {
        var str = switch (packet.getLevel()) {
            case SystemMessageLevel.Pure -> new AttributedStringBuilder();
            case SystemMessageLevel.Info ->
                new AttributedStringBuilder().style(AttributedStyle.DEFAULT.bold());
            case SystemMessageLevel.Success ->
                new AttributedStringBuilder().style(AttributedStyle.DEFAULT.bold().foreground(AttributedStyle.GREEN));
            case SystemMessageLevel.Warning ->
                new AttributedStringBuilder().style(AttributedStyle.DEFAULT.bold().foreground(AttributedStyle.YELLOW));
            case SystemMessageLevel.Error ->
                new AttributedStringBuilder().style(AttributedStyle.DEFAULT.bold().foreground(AttributedStyle.RED));
        };

        var hour = packet.getTime().getHour();
        var minute = packet.getTime().getMinute();
        var second = packet.getTime().getSecond();
        var timeStr = String.format("%02d:%02d:%02d", hour, minute, second);

        str.append("[");
        str.append(timeStr);
        str.append("] ");

        if (packet.getLevel() != SystemMessageLevel.Pure) {
            str.append("<Server> ");
        }

        str.append(packet.getText())
                .style(AttributedStyle.DEFAULT);

        return str.toAnsi();
    }

    private void handleUserMessage(UserMessagePacket packet) {
        String msg = formatUserMessage(packet);
        terminal.write(msg + "\n");
        terminal.flush();
    }

    private String formatUserMessage(UserMessagePacket packet) {
        var str = new AttributedStringBuilder();

        var hour = packet.getTime().getHour();
        var minute = packet.getTime().getMinute();
        var second = packet.getTime().getSecond();
        var timeStr = String.format("%02d:%02d:%02d", hour, minute, second);

        str.append("[");
        str.append(timeStr);
        str.append("] (");
        str.append(packet.getRoomId());
        str.append(") <");
        str.append(packet.getUsername());
        str.append("> ");
        str.append(packet.getText());

        return str.toAnsi();
    }
}
