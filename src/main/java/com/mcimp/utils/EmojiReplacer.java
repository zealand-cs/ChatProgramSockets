package com.mcimp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class EmojiReplacer {
    private HashMap<String, String> emojiLookup;

    public EmojiReplacer(String file) {
        emojiLookup = new HashMap<>();

        var stream = this.getClass().getClassLoader().getResourceAsStream(file);
        try (var inputStream = new InputStreamReader(stream);
                var bufferedInput = new BufferedReader(inputStream)) {

            String line;
            while ((line = bufferedInput.readLine()) != null) {
                var seperated = line.split(";");
                emojiLookup.put(seperated[0], seperated[1]);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String replaceEmojis(String source) {
        var splitted = source.trim().split(" ");
        var builder = new StringBuilder();

        for (var word : splitted) {
            var lookupValue = emojiLookup.getOrDefault(word, word);
            builder.append(lookupValue + " ");
        }

        return builder.toString().trim();
    }
}
