package com.mcimp.protocol.server.packets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.mcimp.protocol.server.ServerPacket;
import com.mcimp.protocol.server.ServerPacketId;

public class FileDownloadPacket extends ServerPacket {
    private final static ServerPacketId PACKET_ID = ServerPacketId.FileDownload;

    private Path path;

    public FileDownloadPacket(Path path) {
        super(PACKET_ID);
        this.path = path;
    }

    public FileDownloadPacket(String file) {
        this(Paths.get(file));
    }

    @Override
    protected void writeToStreamImpl(DataOutputStream stream) throws IOException {
        var fileSize = Files.size(path);
        stream.writeLong(fileSize);

        try (
                var fileInput = new FileInputStream(path.toString());
                var bufferedFileInput = new BufferedInputStream(fileInput);) {
            var buffer = new byte[4 * 1024];

            int fileBytes;
            while ((fileBytes = fileInput.read(buffer)) != -1) {
                stream.write(buffer, 0, fileBytes);
            }
        } catch (IOException ex) {
            throw ex;
        }
    }

    public static void readInputStreamToStream(DataInputStream stream, OutputStream out) throws IOException {
        var fileSize = stream.readLong();

        try (var bufferedFileOutput = new BufferedOutputStream(out)) {
            var buffer = new byte[4 * 1024];

            long totalBytesRead = 0;

            // Math.min used for reading either the entire buffer or only whats left of the
            // file.
            while (totalBytesRead < fileSize) {
                int fileBytes = stream.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalBytesRead));

                bufferedFileOutput.write(buffer, 0, fileBytes);
                totalBytesRead += fileBytes;
            }
        } catch (IOException ex) {
            throw ex;
        }
    }
}
