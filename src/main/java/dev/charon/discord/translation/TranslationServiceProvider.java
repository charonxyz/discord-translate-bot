package dev.charon.discord.translation;


import dev.charon.discord.configuarion.BotConfiguration;
import dev.charon.discord.translation.service.deepl.DeepLTranslationService;
import dev.charon.discord.translation.service.deepl.DeepLTranslationServiceProvider;
import dev.charon.discord.translation.service.google.GoogleTranslationService;
import dev.charon.discord.translation.service.google.GoogleTranslationServiceProvider;
import dev.charon.discord.translation.service.openai.OpenAITranslationService;
import dev.charon.discord.translation.service.openai.OpenAITranslationServiceProvider;
import lombok.Getter;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public class TranslationServiceProvider {

    private final BotConfiguration botConfiguration;

    private GoogleTranslationService googleTranslationService;
    //private MicrosoftTranslationService microsoftTranslationService; // not implemented yet
    private OpenAITranslationService openAITranslationService;
    private DeepLTranslationService deepLTranslationService;

    public TranslationServiceProvider(BotConfiguration botConfiguration) {
        this.botConfiguration = botConfiguration;

        if (botConfiguration.getGoogleAuthKey() != null && !botConfiguration.getGoogleAuthKey().isEmpty()) {
            this.googleTranslationService = new GoogleTranslationServiceProvider(botConfiguration.getGoogleAuthKey());
        }
        if (botConfiguration.getOpenAiAuthKey() != null && !botConfiguration.getOpenAiAuthKey().isEmpty()) {
            this.openAITranslationService = new OpenAITranslationServiceProvider(botConfiguration.getOpenAiAuthKey());
        }
        if (botConfiguration.getDeepLAuthKey() != null && !botConfiguration.getDeepLAuthKey().isEmpty()) {
            this.deepLTranslationService = new DeepLTranslationServiceProvider(botConfiguration.getDeepLAuthKey());
        }
    }
}
