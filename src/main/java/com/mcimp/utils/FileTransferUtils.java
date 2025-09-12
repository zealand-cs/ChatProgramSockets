package com.mcimp.utils;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileTransferUtils {

    private static final Logger logger = LogManager.getLogger(FileTransferUtils.class);


    public static void sendFile(String path, OutputStream out, String role) throws IOException{
        File file = new File(path);


        // Try with resources to ensure all streams are automatically closed
        try (
                // Wraps OutputStream in a DataOutputStream to send primitive data (file length)
                DataOutputStream dataOut = new DataOutputStream(out);
                // Opens fileInputStream to read bytes from the file
                FileInputStream fileIn = new FileInputStream(file);
                // BufferedInputStream to improve the efficiency by buffering chunks of the bytes
                BufferedInputStream bufferedFileIn = new BufferedInputStream(fileIn)) {


            dataOut.writeLong(file.length());
            // 4 * 1024 to do fewer calls, and the increased memory usage won't have an impact on modern day machines
            byte[] buffer = new byte[4 * 1024];
            int bytesRead;
            long totalSent = 0;



            // Read the file in chunks and writes the chunks to the output stream
            while ((bytesRead = bufferedFileIn.read(buffer)) != -1) {
                dataOut.write(buffer, 0, bytesRead);
                totalSent += bytesRead;

                //Currently spamming console with progress on a new line to show that both clients are downloading at the same time, not sure how to do otherwise without a library
                int percent = (int) (totalSent * 100 / file.length());
                System.out.print(
                        "\r[" + role + "] Progress: [" +
                                "=".repeat(percent / 2) +
                                " ".repeat(50 - percent / 2) +
                                "] " + percent + "%"
                );
                // snippet of how it is for a singular progressbar
                //int percent = (int) (totalSent * 100 / file.length());
                //System.out.print("\rProgress: [" + "=".repeat(percent / 2) + " ".repeat(50 - percent / 2) + "] " + percent + "%");

            }


            // FLushes any remaining bytes to the output stream, and it ensures all data is sent before closing
            dataOut.flush();
            logger.info("Sent file: {} {}", file.getName(), role);

        } catch (IOException e) { // All streams closed with this try with resources
            logger.error("Could not send file: {} {}", file.getName(), role, e);
        }
    }

    public static void receiveFile(String path, InputStream inputStream, String role) throws IOException {
        File finalFile = new File(path);
        File tempFile = new File(path + ".tmp");

        try (DataInputStream dataIn = new DataInputStream(inputStream);
             FileOutputStream fileOut = new FileOutputStream(tempFile);
             BufferedOutputStream bufferedOut = new BufferedOutputStream(fileOut)) {


            long fileLength = dataIn.readLong();

            byte[] buffer = new byte[4 * 1024];
            int bytesRead;
            long totalRead = 0;


            while (totalRead < fileLength &&
                    (bytesRead = dataIn.read(buffer, 0, (int) Math.min(buffer.length, fileLength - totalRead))) != -1) {
                bufferedOut.write(buffer, 0, bytesRead);
                totalRead += bytesRead;

                //Currently spamming console with progress on a new line to show that both clients are uploading at the same time
                int percent = (int) ((totalRead * 100) / fileLength);
                System.out.println(
                        "[" + role + "] Receiving: [" +
                                "=".repeat(percent / 2) +
                                " ".repeat(50 - percent / 2) +
                                "] " + percent + "%"
                );
                // snippet of how it is for a singular progressbar
                //int percent = (int) ((totalRead * 100) / fileLength);
                //System.out.print("\rReceiving: [" + "=".repeat(percent / 2) + " ".repeat(50 - percent / 2) + "] " + percent + "%");



            }



            // Flush remaining bytes to ensure the file is completely written
            bufferedOut.flush();
        } catch (IOException e) { // Streams automatically closed with try-with-resources
            logger.error("Could not receive file: {} {}", finalFile.getName(), role,  e);
            return;
        }
        if (finalFile.exists()) {
            finalFile.delete();
        }
        if (tempFile.renameTo(finalFile)) {
            logger.info("Received file: {} {}", finalFile.getName(), role);
        } else {
            logger.error("Could not finalize file: {} {}", finalFile.getName(), role);
        }
    }

}
