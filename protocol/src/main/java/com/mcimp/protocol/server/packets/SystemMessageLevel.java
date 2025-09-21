package com.mcimp.protocol.server.packets;

public enum SystemMessageLevel {
    /**
     * A pure system message with no predetermined styling
     */
    Pure,

    /**
     * An informational system message
     */
    Info,

    /**
     * A success system message
     */
    Success,

    /**
     * A warning system message
     */
    Warning,

    /**
     * An error system message
     */
    Error;

    public byte toByte() {
        return switch (this) {
            case Pure -> 1;
            case Info -> 2;
            case Success -> 3;
            case Warning -> 4;
            case Error -> 5;
        };
    }

    public static SystemMessageLevel fromByte(byte c) {
        return switch (c) {
            case 1 -> Pure;
            case 2 -> Info;
            case 3 -> Success;
            case 4 -> Warning;
            case 5 -> Error;
            default -> throw new RuntimeException("invalid packet type id");
        };
    }
}
