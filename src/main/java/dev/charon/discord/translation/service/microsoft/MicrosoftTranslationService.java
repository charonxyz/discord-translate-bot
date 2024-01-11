package dev.charon.discord.translation.service.microsoft;

import dev.charon.discord.translation.service.TranslationService;
import lombok.Getter;
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
@Getter
public class MicrosoftTranslationService implements TranslationService {

    private final String authkey;

    public MicrosoftTranslationService(String authkey) {
        this.authkey = authkey;
    }

    @Override
    public String translate(@NotNull String text, @Nullable String sourceLanguage, @NotNull List<String> targetLanguages) {
        return "Not implemented yet";
    }

    @Override
    public String getSourceLanguage(@NotNull String text) {
        return null;
    }

}
