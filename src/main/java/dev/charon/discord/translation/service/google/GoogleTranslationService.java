package dev.charon.discord.translation.service.google;

import com.google.cloud.translate.*;
import com.google.common.collect.Lists;
import dev.charon.discord.bot.TranslatorBot;
import dev.charon.discord.translation.TranslateMessage;
import dev.charon.discord.translation.service.TranslationService;
import lombok.Getter;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public class GoogleTranslationService implements TranslationService {

    private final List<SelectOption> languageOptions = Lists.newArrayList();

    private final TranslateOptions translateOptions;

    public GoogleTranslationService(String authKey) {
        System.setProperty("GOOGLE_API_KEY", authKey); // Required for Google Cloud API to work
        translateOptions = TranslateOptions.getDefaultInstance();
        loadSupportedLanguages();
    }

    public String translate(@NotNull String text, @Nullable String sourceLanguage, @NotNull String targetLanguage) {
        Translate translate = translateOptions.getService();

        Translation translation = translate.translate(text, Translate.TranslateOption.sourceLanguage(sourceLanguage), Translate.TranslateOption.targetLanguage(targetLanguage));
        return targetLanguage + ": " + translation.getTranslatedText();
    }

    @Override
    public String translate(@NotNull String text, @Nullable String sourceLanguage, @NotNull List<String> targetLanguages) {
        List<String> translatedTexts = Lists.newArrayList();

        for (String targetLanguage : targetLanguages) {
            translatedTexts.add(translate(text, sourceLanguage, targetLanguage));
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String translatedText : translatedTexts) {
            stringBuilder.append(translatedText).append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public String getSourceLanguage(@NotNull String text) {
        Detection detection = translateOptions.getService().detect(text);
        if (Objects.isNull(detection))
            throw new IllegalArgumentException("Invalid text");
        return detection.getLanguage();
    }

    public void loadSupportedLanguages() {
        Translate translate = translateOptions.getService();
        List<Language> languages = translate.listSupportedLanguages();

        for (Language language : languages) {
            // only load standard languages
            if ((!language.getName().equalsIgnoreCase("english") &&
                    !language.getName().equalsIgnoreCase("french") &&
                    !language.getName().equalsIgnoreCase("spanish") &&
                    !language.getName().equalsIgnoreCase("italian") &&
                    !language.getName().equalsIgnoreCase("german") &&
                    !language.getName().equalsIgnoreCase("portuguese") &&
                    !language.getName().equalsIgnoreCase("russian") &&
                    !language.getName().equalsIgnoreCase("chinese") &&
                    !language.getName().equalsIgnoreCase("japanese") &&
                    !language.getName().equalsIgnoreCase("arabic") &&
                    !language.getName().equalsIgnoreCase("turkish") &&
                    !language.getName().equalsIgnoreCase("polish") &&
                    !language.getName().equalsIgnoreCase("dutch") &&
                    !language.getName().equalsIgnoreCase("danish"))) {
                //System.out.println("Skipping " + language.getName());
                continue;
            }

            String code = language.getCode();
            if (code.equalsIgnoreCase("en")) {
                code = "GB";
            }
            String formattedCode = TranslatorBot.getInstance().getEmoji(code.toUpperCase());
            if (formattedCode == null) {
                TranslatorBot.LOGGER.severe("Could not find emoji for language: " + language.getName() + " (" + code + ")");
                continue;
            }

            UnicodeEmoji emoji = Emoji.fromUnicode(formattedCode);
            if (language.getName().equalsIgnoreCase("Arabic") || code.equalsIgnoreCase("AE")) {
                emoji = Emoji.fromUnicode("🇦🇪");
                code = "ae";
            }
            if (language.getName().equalsIgnoreCase("Luxembourgish") || code.equalsIgnoreCase("LU")) {
                emoji = Emoji.fromUnicode("🇱🇺");
                code = "lu";
            }
            if (language.getName().equalsIgnoreCase("Filipino")) {
                emoji = Emoji.fromUnicode("🇵🇭");
                code = "ph";
            }
            SelectOption selectOption = SelectOption.of(language.getName(), code).withDescription("Language code: " + language.getCode())
                    .withEmoji(emoji);
            if (languageOptions.contains(selectOption)) {
                continue;
            }

            languageOptions.add(selectOption);
        }
    }


}
