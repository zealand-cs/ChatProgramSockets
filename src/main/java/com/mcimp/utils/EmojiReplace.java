package com.mcimp.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class EmojiReplace {
    private HashMap<String, String> emojiLookup;

    public EmojiReplace(String file) {
        emojiLookup = new HashMap<>();

        try {
            Files.lines(Paths.get(file)).forEach(v -> {
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
