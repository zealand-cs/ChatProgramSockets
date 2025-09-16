package com.mcimp.protocol.messages;

public enum MessageType {
    Text,
    Emoji,
    File;

    public byte toByte() {
        return switch (this) {
            case Text -> 1;
            case Emoji -> 2;
            case File -> 3;
        };
    }

    public static MessageType fromByte(byte c) {
        return switch (c) {
            case 1 -> Text;
            case 2 -> Emoji;
            case 3 -> File;
            default -> throw new RuntimeException("invalid packet type id");
        };
    }
}
