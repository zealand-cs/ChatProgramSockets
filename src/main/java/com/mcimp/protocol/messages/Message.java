package com.mcimp.protocol.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;

import com.mcimp.protocol.Packet;
import com.mcimp.protocol.PacketType;

public abstract class Message extends Packet {
    private final static PacketType PACKET_TYPE = PacketType.Message;

    private MessageType messageType;
    private long epochSecond;

    public Message(MessageType messageType, long epochSecond) {
        super(PACKET_TYPE);
        this.messageType = messageType;
        this.epochSecond = epochSecond;
    }

    public Message(MessageType messageType) {
        this(messageType, ZonedDateTime.now().toEpochSecond());
    }

    public MessageType getMessageType() {
        return messageType;
    }

    @Override
    public void writeToStream(DataOutputStream stream) throws IOException {
        super.writeToStream(stream);
        stream.writeByte(messageType.toByte());
        stream.writeLong(epochSecond);
    }

    public static Message readMessage(DataInputStream stream) throws IOException {
        var type = MessageType.fromByte(stream.readByte());
        var epoch = stream.readLong();

        Message message = switch (type) {
            case MessageType.System -> SystemMessage.readFromStream(stream);
            case MessageType.Text -> TextMessage.readFromStream(stream);
            case MessageType.Emoji -> EmojiMessage.readFromStream(stream);
            case MessageType.File ->
                throw new RuntimeException("not implemented yet");
        };
        message.setEpochSecond(epoch);
        return message;
    }

    public long getEpochSecond() {
        return epochSecond;
    }

    public void setEpochSecond(long epoch) {
        this.epochSecond = epoch;
    }
}
