package com.mcimp.protocol.client.packets;

public enum AuthenticationType {
    Register,
    Login;

    public byte toByte() {
        return switch (this) {
            case Register -> 1;
            case Login -> 2;
        };
    }

    public static AuthenticationType fromByte(byte c) {
        return switch (c) {
            case 1 -> Register;
            case 2 -> Login;
            default -> throw new RuntimeException("invalid auth type");
        };
    }
}
