package com.mcimp.protocol;

public class Packets {
    public static final byte SERVER_PACKET_START           = 0x01;
    public static final byte SERVER_PACKET_END             = 0x3F;
    public static final byte CLIENT_PACKET_START           = 0x40;
    public static final byte CLIENT_PACKET_END             = 0x7F;

    /// Null packets could be usefull sometime
    public static final byte NULL_PACKET                   = 0x00;

    // Server packets
    public static final byte SERVER_CONNECTED              = 0x01;
    public static final byte SERVER_DISCONNECTED           = 0x02;
    public static final byte SERVER_SYSTEM_MESSAGE         = 0x03;
    public static final byte SERVER_ROOM_MESSAGE           = 0x04;

    // Client packets
    public static final byte CLIENT_CONNECT                = 0x40;
    public static final byte CLIENT_DISCONNECT             = 0x41;
    public static final byte CLIENT_AUTHENTICATE           = 0x42;
    public static final byte CLIENT_JOIN_ROOM              = 0x43;
    public static final byte CLIENT_ROOM_DETAILS           = 0x44;
    public static final byte CLIENT_LIST_ROOMS             = 0x45;
    public static final byte CLIENT_LIST_USERS             = 0x46;
    public static final byte CLIENT_SEND_MESSAGE           = 0x47;
    public static final byte CLIENT_FILE_UPLOAD            = 0x48;
    public static final byte CLIENT_FILE_DOWNLOAD_REQUEST  = 0x49;

    public static boolean isServerPacket(byte p) {
        return p >= SERVER_PACKET_START && p <= SERVER_PACKET_END;
    }

    public static boolean isClientPacket(byte p) {
        return p >= CLIENT_PACKET_START && p <= CLIENT_PACKET_END;
    }
}
