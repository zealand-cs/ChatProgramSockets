package com.mcimp.protocol.server.packets;

public enum RoomMessageType {
    User;

    public byte toByte() {
        return switch (this) {
            case User -> 1;
        };
    }

    public static RoomMessageType fromByte(byte c) {
        return switch (c) {
            case 1 -> User;
            default -> throw new RuntimeException("invalid packet type id");
        };
    }
}
