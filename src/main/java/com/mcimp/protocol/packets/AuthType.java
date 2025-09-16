package com.mcimp.protocol.packets;

public enum AuthType {
    Register,
    Login;

    public byte toByte() {
        return switch (this) {
            case Register -> 1;
            case Login -> 2;
        };
    }

    public static AuthType fromByte(byte c) {
        return switch (c) {
            case 1 -> Register;
            case 2 -> Login;
            default -> throw new RuntimeException("invalid auth type");
        };
    }
}
