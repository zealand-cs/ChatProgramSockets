package com.mcimp.protocol.server.packets;

public enum UserMessageScope {
    Global,
    Room,
    User;

    public byte toByte() {
        return switch (this) {
            case Global -> 1;
            case Room -> 2;
            case User -> 3;
        };
    }

    public static UserMessageScope fromByte(byte c) {
        return switch (c) {
            case 1 -> Global;
            case 2 -> Room;
            case 3 -> User;
            default -> throw new RuntimeException("invalid system scope");
        };
    }
}
