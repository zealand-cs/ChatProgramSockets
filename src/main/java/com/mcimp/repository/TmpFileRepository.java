package com.mcimp.repository;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class TmpFileRepository implements AutoCloseable {
    private final Path storageDirectory;
    private Map<String, Path> files;

    private final Random random = new Random();

    public TmpFileRepository(Path directory) {
        this.storageDirectory = directory;

        // Create directory if it doesn't exist
        try {
            Files.createDirectory(directory);
        } catch (FileAlreadyExistsException ex) {
            // Ignore. This is good.
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        this.files = new HashMap<>();
    }

    public TmpFileRepository(String directory) {
        this(Paths.get(directory));
    }

    /**
     * Writes a temporary file. Should be cleaned up, at least when closing
     * server.
     */
    public String createFileId() throws IOException {
        var id = generateString(random, "0123456789", 6);
        var filePath = storageDirectory.resolve(id);
        var path = Files.createFile(filePath);
        files.put(id, path);
        return id;
    }

    public OutputStream fileStream(String id) throws IOException {
        var path = storageDirectory.resolve(id);
        return Files.newOutputStream(path);
    }

    public Optional<Path> getFilePath(String id) {
        return Optional.ofNullable(files.get(id));
    }

    public static String generateString(Random rng, String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

    @Override
    public void close() throws IOException {
        // Memory leak if an exception occours?
        var dir = Files.newDirectoryStream(storageDirectory);
        for (var entry : dir) {
            Files.delete(entry);
        }
        dir.close();
    }
}
