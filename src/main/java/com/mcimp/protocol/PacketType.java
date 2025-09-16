package com.mcimp.protocol;

public enum PacketType {
    Connect,
    Connected,
    Disconnect,
    Disconnected,
    // For further implementation
    Banned,
    // For further implementation
    Unbanned,

    Command,
    Message;

    public byte toByte() {
        return switch (this) {
            case Connect -> 1;
            case Connected -> 2;
            case Disconnect -> 3;
            case Disconnected -> 4;
            case Banned -> 5;
            case Unbanned -> 6;

            case Command -> 100;
            case Message -> 101;
        };
    }

    public static PacketType fromByte(byte c) {
        return switch (c) {
            case 1 -> Connect;
            case 2 -> Connected;
            case 3 -> Disconnect;
            case 4 -> Disconnected;
            case 5 -> Banned;
            case 6 -> Unbanned;

            case 100 -> Command;
            case 101 -> Message;
            default -> throw new RuntimeException("invalid packet type id");
        };
    }
}
