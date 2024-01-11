package dev.charon.discord.translation.service.deepl;

import com.deepl.api.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.charon.discord.bot.TranslatorBot;
import dev.charon.discord.translation.service.TranslationService;
import lombok.Getter;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public class DeepLTranslationService implements TranslationService {

    private final Translator translator;

    private final HashMap<String, String> languageCodes = Maps.newHashMap();
    private final List<SelectOption> languageOptions = Lists.newArrayList();

    public DeepLTranslationService(String authKey) {
        this.translator = new Translator(authKey, getOptions());

        loadLanguageOptions();
    }

    private TranslatorOptions getOptions() {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("User-Agent", "TranslatorBot/1.0.0");
        return new TranslatorOptions()
                .setAppInfo("Discord-TranslatorBot", "1.0.0")
                .setSendPlatformInfo(false).setHeaders(headers);
    }

    private void loadLanguageOptions() {
        languageCodes.put("German", LanguageCode.German);
        languageCodes.put("French", LanguageCode.French);
        languageCodes.put("Spanish",LanguageCode.Spanish);
        languageCodes.put("Italian", LanguageCode.Italian);
        languageCodes.put("Dutch", LanguageCode.Dutch);
        languageCodes.put("Polish", LanguageCode.Polish);
        languageCodes.put("Russian", LanguageCode.Russian);
        languageCodes.put("Japanese", LanguageCode.Japanese);
        languageCodes.put("Chinese", LanguageCode.Chinese);
        languageCodes.put("Bulgarian", LanguageCode.Bulgarian);
        languageCodes.put("Czech", LanguageCode.Czech);
        languageCodes.put("Danish",LanguageCode.Danish);
        languageCodes.put("Estonian", LanguageCode.Estonian);
        languageCodes.put("Finnish", LanguageCode.Finnish);
        languageCodes.put("Greek", LanguageCode.Greek);
        languageCodes.put("Hungarian", LanguageCode.Hungarian);
        languageCodes.put("Latvian", LanguageCode.Latvian);
        languageCodes.put("Lithuanian", LanguageCode.Lithuanian);
        languageCodes.put("Romanian", LanguageCode.Romanian);
        languageCodes.put("Slovak", LanguageCode.Slovak);
        languageCodes.put("Slovenian", LanguageCode.Slovenian);
        languageCodes.put("Swedish", LanguageCode.Swedish);
        languageCodes.put("Turkish", LanguageCode.Turkish);
        languageCodes.put("English-American", LanguageCode.EnglishAmerican);
        languageCodes.put("English-British", LanguageCode.EnglishBritish);
        languageCodes.put("Portuguese-Brazilian", LanguageCode.PortugueseBrazilian);
        languageCodes.put("Portuguese-European", LanguageCode.PortugueseEuropean);

        // Currently only 25 languages are available due to Discord's limit of 25 options per select menu -> Current Languages 27
        // TODO: Find a other option to add more languages
        for (var languageCode : languageCodes.entrySet()) {
            String value = languageCode.getValue();
            if (value.startsWith("en-") || value.startsWith("pt-")) value = value.split("-")[1];

            String formattedCode = TranslatorBot.getInstance().getEmoji(value.toUpperCase());
            if (formattedCode == null) {
                System.out.println("Emoji not found for language: " + value);
                continue;
            }

            UnicodeEmoji emoji = Emoji.fromUnicode(formattedCode);
            languageOptions.add(SelectOption.of(languageCode.getKey(),
                    languageCode.getValue())
                    .withDescription("Language code: " + languageCode.getValue())
                    .withEmoji(emoji));
        }
    }

    public String translate(@NotNull String text, @Nullable String sourceLanguage, @NotNull String targetLanguage) {
        try {
            TextResult result = translator.translateText(text, sourceLanguage, targetLanguage);
            return targetLanguage + ": " + result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String translate(@NotNull String text, @Nullable String sourceLanguage, @NotNull List<String> targetLanguages) {
        List<String> translatedTexts = Lists.newArrayList();
        for (String targetLanguage : targetLanguages) {
            translatedTexts.add(translate(text, sourceLanguage, targetLanguage));
            System.out.println(targetLanguage);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String translatedText : translatedTexts) {
            stringBuilder.append(translatedText).append("\n");
            System.out.println(translatedText);
        }
        return stringBuilder.toString();
    }

    @Override
    public String getSourceLanguage(@NotNull String text) {
        for (var languageCode : languageCodes.entrySet()) {
            if (languageCode.getValue().equals(text)) {
                return languageCode.getKey();
            }
        }
        return null;
    }
}
