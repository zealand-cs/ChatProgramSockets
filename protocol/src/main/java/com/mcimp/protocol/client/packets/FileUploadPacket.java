package com.mcimp.protocol.client.packets;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.mcimp.protocol.client.ClientPacket;
import com.mcimp.protocol.client.ClientPacketId;

public class FileUploadPacket extends ClientPacket {
    private final static ClientPacketId PACKET_ID = ClientPacketId.FileUpload;

    private Path path;

    public FileUploadPacket(Path path) {
        super(PACKET_ID);
        this.path = path;
    }

    public FileUploadPacket(String file) {
        this(Paths.get(file));
    }

    @Override
    protected void writeToStreamImpl(DataOutputStream input) throws IOException {
        var fileSize = Files.size(path);
        input.writeLong(fileSize);

        try (var bufferedInput = new BufferedInputStream(Files.newInputStream(path))) {
            // Buffer of 4kb
            var buffer = new byte[4 * 1024];
            int fileBytes;
            while ((fileBytes = bufferedInput.read(buffer)) != -1) {
                input.write(buffer, 0, fileBytes);
            }
        } catch (IOException ex) {
            throw ex;
        }
    }

    public static void readInputStreamToStream(DataInputStream input, OutputStream output) throws IOException {
        var fileSize = input.readLong();

        // Buffer of 4kb
        var buffer = new byte[4 * 1024];

        long totalBytesRead = 0;

        // Math.min used for reading either the entire buffer or only whats left of the
        // file.
        var bytes = 0;
        while (totalBytesRead < fileSize && (bytes = input.read(buffer, 0,
                (int) Math.min(buffer.length, fileSize - totalBytesRead))) != -1) {
            output.write(buffer, 0, bytes);
            totalBytesRead += bytes;
        }
    }
}
