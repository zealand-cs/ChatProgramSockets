package com.mcimp.protocol.client.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mcimp.protocol.client.ClientPacket;
import com.mcimp.protocol.client.ClientPacketId;

public class FileDownloadRequestPacket extends ClientPacket {
    private final static ClientPacketId PACKET_ID = ClientPacketId.FileDownloadRequest;

    private String fileId;

	public FileDownloadRequestPacket(String fileId) {
        super(PACKET_ID);
        this.fileId = fileId;
    }

    @Override
    protected void writeToStreamImpl(DataOutputStream stream) throws IOException {
        stream.writeUTF(fileId);
    }

    public static FileDownloadRequestPacket readFromStream(DataInputStream stream) throws IOException {
        var fileId = stream.readUTF();
        return new FileDownloadRequestPacket(fileId);
    }

    public String getFileId() {
		return fileId;
	}
}
