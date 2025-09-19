package com.mcimp.protocol.client;

import com.mcimp.protocol.Packets;

// Packets sent from the client to the server
// Client -> Server
public enum ClientPacketId {
    Connect,
    Disconnect,
    Authenticate,
    JoinRoom,
    Message;

    public byte toByte() {
        return switch (this) {
            case Connect -> Packets.CLIENT_CONNECT;
            case Disconnect -> Packets.CLIENT_DISCONNECT;
            case Authenticate -> Packets.CLIENT_AUTHENTICATE;

            case JoinRoom -> Packets.CLIENT_JOIN_ROOM;
            case Message -> Packets.CLIENT_SEND_MESSAGE;
        };
    }

    public static ClientPacketId fromByte(byte c) {
        return switch (c) {
            case Packets.CLIENT_CONNECT -> Connect;
            case Packets.CLIENT_DISCONNECT -> Disconnect;
            case Packets.CLIENT_AUTHENTICATE -> Authenticate;

            case Packets.CLIENT_JOIN_ROOM -> JoinRoom;
            case Packets.CLIENT_SEND_MESSAGE -> Message;
            default -> throw new RuntimeException("invalid client packet id");
        };
    }
}
