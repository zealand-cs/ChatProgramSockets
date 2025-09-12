package com.mcimp.protocol;

enum PacketType {
    Connected,
    Disconnected,
    // For further implementation
    Banned,
    // For further implementation
    Unbanned,

    Command,
    Message;

    public byte toByte() {
        return switch (this) {
            case Connected -> 1;
            case Disconnected -> 2;
            case Banned -> 3;
            case Unbanned -> 4;

            case Command -> 100;
            case Message -> 101;
        };
    }

    public static PacketType fromByte(byte c) {
        return switch (c) {
            case 1 -> Connected;
            case 2 -> Disconnected;
            case 3 -> Banned;
            case 4 -> Unbanned;
            case 100 -> Command;
            case 101 -> Message;
            default -> throw new RuntimeException("invalid packet type id");
        };
    }
}
