package com.mcimp.protocol.server;

import com.mcimp.protocol.Packets;

// Packets sent from the server to the clients
// Server -> Client
public enum ServerPacketId {
    Connected,
    Disconnected,
    SystemMessage,
    UserMessage,
    FileMetadata,
    FileDownload;

    public String getName() {
        return this.toString();
    }

    public byte toByte() {
        return switch (this) {
            case Connected -> Packets.SERVER_CONNECTED;
            case Disconnected -> Packets.SERVER_DISCONNECTED;
            case SystemMessage -> Packets.SERVER_SYSTEM_MESSAGE;
            case UserMessage -> Packets.SERVER_ROOM_MESSAGE;
            case FileMetadata -> Packets.SERVER_FILE_METADATA;
            case FileDownload -> Packets.SERVER_FILE_DOWNLOAD;
        };
    }

    public static ServerPacketId fromByte(byte c) {
        return switch (c) {
            case Packets.SERVER_CONNECTED -> Connected;
            case Packets.SERVER_DISCONNECTED -> Disconnected;
            case Packets.SERVER_SYSTEM_MESSAGE -> SystemMessage;
            case Packets.SERVER_ROOM_MESSAGE -> UserMessage;
            case Packets.SERVER_FILE_METADATA -> FileMetadata;
            case Packets.SERVER_FILE_DOWNLOAD -> FileDownload;
            default -> throw new RuntimeException("invalid server packet id");
        };
    }
}
