package dev.charon.discord.translation.service;

import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public interface TranslationService {

    /**
     * @param text            - the text to translate
     * @param sourceLanguage  - the language to translate from, can be null
     * @param targetLanguages - the language to translate to
     * @return the translated text
     **/
    String translate(@NotNull String text, @Nullable String sourceLanguage, @NotNull List<String> targetLanguages);

    /**
     * @param text - the text to translate
     * @return the language code of the source language
     */
    String getSourceLanguage(@NotNull String text);

}
