package com.mcimp.protocol.client;

import com.mcimp.protocol.Packets;

// Packets sent from the client to the server
// Client -> Server
public enum ClientPacketId {
    Connect,
    Disconnect,
    Authenticate,
    JoinRoom,
    RoomDetails,
    ListRooms,
    ListUsers,
    Message,
    FileUpload,
    FileDownloadRequest;

    public byte toByte() {
        return switch (this) {
            case Connect -> Packets.CLIENT_CONNECT;
            case Disconnect -> Packets.CLIENT_DISCONNECT;
            case Authenticate -> Packets.CLIENT_AUTHENTICATE;

            case JoinRoom -> Packets.CLIENT_JOIN_ROOM;
            case RoomDetails -> Packets.CLIENT_ROOM_DETAILS;
            case ListRooms -> Packets.CLIENT_LIST_ROOMS;
            case ListUsers -> Packets.CLIENT_LIST_USERS;

            case Message -> Packets.CLIENT_SEND_MESSAGE;
            case FileUpload -> Packets.CLIENT_FILE_UPLOAD;
            case FileDownloadRequest -> Packets.CLIENT_FILE_DOWNLOAD_REQUEST;
        };
    }

    public static ClientPacketId fromByte(byte c) {
        return switch (c) {
            case Packets.CLIENT_CONNECT -> Connect;
            case Packets.CLIENT_DISCONNECT -> Disconnect;
            case Packets.CLIENT_AUTHENTICATE -> Authenticate;

            case Packets.CLIENT_JOIN_ROOM -> JoinRoom;
            case Packets.CLIENT_ROOM_DETAILS -> RoomDetails;
            case Packets.CLIENT_LIST_ROOMS -> ListRooms;
            case Packets.CLIENT_LIST_USERS -> ListUsers;

            case Packets.CLIENT_SEND_MESSAGE -> Message;
            case Packets.CLIENT_FILE_UPLOAD -> FileUpload;
            case Packets.CLIENT_FILE_DOWNLOAD_REQUEST -> FileDownloadRequest;
            default -> throw new RuntimeException("invalid client packet id");
        };
    }
}
