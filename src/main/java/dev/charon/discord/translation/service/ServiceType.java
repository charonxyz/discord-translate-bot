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

    GOOGLE(TranslatorBot.getInstance().getTranslationService().getGoogleTranslationService().getLanguageOptions().size()),
    MICROSOFT(0), // not implemented yet
    OPENAI(TranslatorBot.getInstance().getTranslationService().getGoogleTranslationService().getLanguageOptions().size()),
    DEEPL(TranslatorBot.getInstance().getTranslationService().getDeepLTranslationService().getLanguageOptions().size());

    private final int supportedLanguages;

    ServiceType(int supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
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
        return List.of("Google", "OpenAI", "DeepL");
    }
}
