package dev.charon.discord.language;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import dev.charon.discord.bot.TranslatorBot;
import dev.charon.discord.language.unicode.UnicodeProvider;
import dev.charon.discord.language.unicode.UnicodeService;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static dev.charon.discord.bot.TranslatorBot.LOGGER;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class UnicodeLoader {

    private final TranslatorBot translatorBot;

    @Getter
    private UnicodeProvider unicodeProvider;

    public UnicodeLoader(TranslatorBot translatorBot) {
        this.translatorBot = translatorBot;
    }

    @SneakyThrows
    public void loadUnicodeProvider(File file) {
        if (file.mkdirs()) {
            LOGGER.info("Created bot folder");
        }

        final Path path = Paths.get("bot/unicodes.json");
        if (!Files.exists(path)) {
            Files.copy(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("unicodes.json")), path);
            LOGGER.info("Created unicodes.json");
        }

        try {
            this.unicodeProvider = translatorBot.getGson().fromJson(Files.newBufferedReader(path), UnicodeProvider.class);
        } catch (Exception e) {
            e.printStackTrace(); // Print the exception details
            LOGGER.severe("Failed to load unicodes.json: " + e.getMessage());
            System.exit(0);
        }
    }

    @SneakyThrows
    public void saveUnicodeProvider() {
        final Path path = Paths.get("bot/unicodes.json");
        if (!Files.exists(path)) {
            Files.copy(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("unicodes.json")), path);
            LOGGER.info("Created unicodes.json");
        }

        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             JsonWriter writer = new JsonWriter(osw)) {

            String json = translatorBot.getGson().toJson(unicodeProvider);
            JsonElement jsonElement = JsonParser.parseString(json);
            translatorBot.getGson().toJson(jsonElement, writer);

            LOGGER.info("JSON saved to: " + path.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public void loadFlags() {
        if (unicodeProvider.getUnicode().isEmpty()) { // If the unicodes.json is empty, load the flags
            for (UnicodeService.Emoji emoji : UnicodeService.extractFlags()) {
                String countryCode = getCountryCodeFromApi(emoji.getTitle().replace(" ", "%20"));

                if (countryCode == null || countryCode.equalsIgnoreCase("N/A")) {
                    continue;
                }
                if (!countryCode.equalsIgnoreCase("/A") && !unicodeProvider.getUnicode().containsKey(countryCode)) {
                    unicodeProvider.getUnicode().put(countryCode, emoji.getCharacter());
                }
            }
            saveUnicodeProvider();
        }
        LOGGER.info("Finished Loading!\nLoaded Flags: " + unicodeProvider.getUnicode().size());
    }


    private String getCountryCodeFromApi(String countryName) {
        String apiUrl = "https://restcountries.com/v3.1/name/" + countryName;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            connection.disconnect();

            return parseCCAFromResponse(response.toString());
        } catch (Exception e) {
            return "N/A";
        }
    }

    private static String parseCCAFromResponse(String response) {
        try {
            JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
            if (!jsonArray.isEmpty()) {
                JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                if (jsonObject.has("cca2")) {
                    return jsonObject.get("cca2").getAsString();
                }
            }
        } catch (JsonParseException e) {
            e.printStackTrace(); // Print the exception details for debugging
        }
        return "N/A";
    }

}
