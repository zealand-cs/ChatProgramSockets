package com.mcimp.protocol.client.packets;

import java.io.DataOutputStream;
import java.io.IOException;

import com.mcimp.protocol.client.ClientPacket;
import com.mcimp.protocol.client.ClientPacketId;

public class FileUploadReceivePacket extends ClientPacket {
    private final static ClientPacketId PACKET_ID = ClientPacketId.FileUpload;
    private long fileSize;

    public FileUploadReceivePacket(long fileSize) {
        super(PACKET_ID);
        this.fileSize = fileSize;
    }

    @Override
    protected void writeToStreamImpl(DataOutputStream stream) throws IOException {
        throw new RuntimeException("file upload receive should never be written to a stream");
    }
}
