package com.mcimp.protocol.commands;

public enum CommandType {
    Join;

    public byte toByte() {
        return switch (this) {
            case Join -> 1;
        };
    }

    public static CommandType fromByte(byte c) {
        return switch (c) {
            case 1 -> Join;
            default -> throw new RuntimeException("invalid packet type id");
        };
    }
}
