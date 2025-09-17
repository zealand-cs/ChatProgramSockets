package com.mcimp.protocol.messages;

public enum SystemMessageLevel {
    Info,
    Warning,
    Error;

    public byte toByte() {
        return switch (this) {
            case Info -> 1;
            case Warning -> 2;
            case Error -> 3;
        };
    }

    public static SystemMessageLevel fromByte(byte c) {
        return switch (c) {
            case 1 -> Info;
            case 2 -> Warning;
            case 3 -> Error;
            default -> throw new RuntimeException("invalid packet type id");
        };
    }
}
