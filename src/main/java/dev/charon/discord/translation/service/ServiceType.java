package dev.charon.discord.translation.service;

import dev.charon.discord.bot.TranslatorBot;
import lombok.Getter;

import java.util.List;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public enum ServiceType {

    GOOGLE(TranslatorBot.getInstance().getTranslationService().getGoogleTranslationService() != null ?
            TranslatorBot.getInstance().getTranslationService().getGoogleTranslationService().getLanguageOptions().size() : 0,
            "Google Translator (https://translate.google.com/)", "\uD83C\uDF10"),
    MICROSOFT(0, "", ""), // not implemented yet
    OPENAI(TranslatorBot.getInstance().getTranslationService().getOpenAITranslationService() != null ?
            TranslatorBot.getInstance().getTranslationService().getOpenAITranslationService().getLanguageOptions().size() : 0,
            "OpenAI's GPT-3.5 (https://chat.openai.com)", "🤖"),
    DEEPL(TranslatorBot.getInstance().getTranslationService().getDeepLTranslationService() != null ?
            TranslatorBot.getInstance().getTranslationService().getDeepLTranslationService().getLanguageOptions().size() : 0,
            "DeepL's neural machine translation (https://www.deepl.com/translator)", "🌍");

    private final int supportedLanguages;
    private final String description;
    private final String emoji;

    ServiceType(int supportedLanguages, String description, String emoji) {
        this.supportedLanguages = supportedLanguages;
        this.description = description;
        this.emoji = emoji;
    }

    public static ServiceType fromString(String string) {
        for (ServiceType serviceType : values()) {
            if (serviceType.name().equalsIgnoreCase(string)) {
                return serviceType;
            }
        }
        return null;
    }

    public static List<String> getServices() {
        StringBuilder services = new StringBuilder();
        for (ServiceType serviceType : values()) {
            if (serviceType.getSupportedLanguages() > 0)
                services.append(serviceType.name()).append(", ");
        }
        return List.of(services.substring(0, services.toString().length() - 2));
    }
}
