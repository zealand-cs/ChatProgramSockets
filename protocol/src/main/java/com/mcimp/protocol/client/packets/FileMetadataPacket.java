package com.mcimp.protocol.client.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.mcimp.protocol.client.ClientPacket;
import com.mcimp.protocol.client.ClientPacketId;

public class FileMetadataPacket extends ClientPacket {
    private final static ClientPacketId PACKET_ID = ClientPacketId.FileMetadata;

    private String fileName;
    private long size;

    public FileMetadataPacket(String fileName, long size) {
        super(PACKET_ID);
        this.fileName = fileName;
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
}
