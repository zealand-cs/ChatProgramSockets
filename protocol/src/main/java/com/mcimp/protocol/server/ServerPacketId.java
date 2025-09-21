package com.mcimp.protocol.server;

import com.mcimp.protocol.Packets;

// Packets sent from the server to the clients
// Server -> Client
public enum ServerPacketId {
    Connected,
    Disconnected,
    SystemMessage,
    UserMessage;

    public byte toByte() {
        return switch (this) {
            case Connected -> Packets.SERVER_CONNECTED;
            case Disconnected -> Packets.SERVER_DISCONNECTED;
            case SystemMessage -> Packets.SERVER_SYSTEM_MESSAGE;
            case UserMessage -> Packets.SERVER_ROOM_MESSAGE;
        };
    }

    public static ServerPacketId fromByte(byte c) {
        return switch (c) {
            case Packets.SERVER_CONNECTED -> Connected;
            case Packets.SERVER_DISCONNECTED -> Disconnected;
            case Packets.SERVER_SYSTEM_MESSAGE -> SystemMessage;
            case Packets.SERVER_ROOM_MESSAGE -> UserMessage;
            default -> throw new RuntimeException("invalid server packet id");
        };
    }
}
