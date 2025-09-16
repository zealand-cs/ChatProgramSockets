package com.mcimp.protocol.messages;

public enum MessageType {
    System,
    Text,
    Emoji,
    File;

    public byte toByte() {
        return switch (this) {
            case System -> 1;
            case Text -> 2;
            case Emoji -> 3;
            case File -> 4;
        };
    }

    public static MessageType fromByte(byte c) {
        return switch (c) {
            case 1 -> System;
            case 2 -> Text;
            case 3 -> Emoji;
            case 4 -> File;
            default -> throw new RuntimeException("invalid packet type id");
        };
    }
}
