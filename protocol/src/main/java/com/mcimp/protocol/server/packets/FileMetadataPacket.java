package com.mcimp.protocol.server.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.mcimp.protocol.server.ServerPacket;
import com.mcimp.protocol.server.ServerPacketId;

public class FileMetadataPacket extends ServerPacket {
    private final static ServerPacketId PACKET_ID = ServerPacketId.FileMetadata;

    private String fileName;
    private long size;

    public FileMetadataPacket(String fileName, long size) {
        super(PACKET_ID);
        this.fileName = fileName;
        this.size = size;
    }

    public FileMetadataPacket(Path path) throws IOException {
        this(path.getFileName().toString(), Files.size(path));
    }

    @Override
    protected void writeToStreamImpl(DataOutputStream stream) throws IOException {
        stream.writeUTF(fileName);
        stream.writeLong(size);
    }

    public static FileMetadataPacket readFromStream(DataInputStream stream) throws IOException {
        var fileName = stream.readUTF();
        var size = stream.readLong();
        return new FileMetadataPacket(fileName, size);
    }

    public String getFileName() {
        return fileName;
    }

    public long getSize() {
        return size;
    }
}
