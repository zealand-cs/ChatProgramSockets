package com.mcimp.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class EmojiReplacer {
    private HashMap<String, String> emojiLookup;

    public EmojiReplacer(String file) {
        emojiLookup = new HashMap<>();

        var filePath = this.getClass().getResource(file).getPath();

        try {
            Files.lines(Paths.get(filePath)).forEach(v -> {
                var seperated = v.split(";");
                emojiLookup.put(seperated[0], seperated[1]);
            });
        } catch (IOException e) {
            throw new RuntimeException("error while reading emoji lookup table: " + e);
        }
    }

    public String replaceEmojis(String source) {
        var splitted = source.trim().split(" ");
        var builder = new StringBuilder();

        for (var word : splitted) {
            var lookupValue = emojiLookup.getOrDefault(word, word);
            builder.append(" " + lookupValue);
        }

        return builder.toString();
    }
}
