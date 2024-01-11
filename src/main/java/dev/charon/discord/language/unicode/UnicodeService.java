package dev.charon.discord.language.unicode;

import com.google.gson.*;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Reader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dev.charon.discord.bot.TranslatorBot.LOGGER;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class UnicodeService {

    @Getter
    @Setter
    public static class Emoji {
        private String name;
        private String title;
        private String character;

        public Emoji(String name, String title, String character) {
            this.name = name;
            this.title = title;
            this.character = character;
        }
    }

    public static List<Emoji> extractFlags() throws IOException {
        List<Emoji> emojis = new ArrayList<>();


        // there is a better way to do this, but i'm too lazy to do it
        final Path path = Paths.get("bot/emoji.json");

        if (!Files.exists(path)) {
            Files.copy(Objects.requireNonNull(UnicodeService.class.getClassLoader().getResourceAsStream("emoji.json")), path);
            LOGGER.info("Created emoji.json");
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();

            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                JsonArray aliasesArray = jsonObject.getAsJsonArray("aliases");
                String description = jsonObject.getAsJsonPrimitive("description").getAsString();

                if (aliasesArray != null && description.startsWith("flag:")) {
                    String emoji = jsonObject.getAsJsonPrimitive("emoji").getAsString();
                    String title = description.replace("flag:", "").trim();
                    emojis.add(new Emoji(emoji, title, emoji));
                    System.out.println("Added " + title);
                }
            }
        }

        return emojis;
    }

}
