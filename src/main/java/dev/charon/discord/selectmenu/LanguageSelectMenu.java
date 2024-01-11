package dev.charon.discord.selectmenu;

import com.google.common.collect.Lists;
import dev.charon.discord.translation.TranslationServiceProvider;
import dev.charon.discord.translation.service.ServiceType;
import dev.charon.discord.translation.service.TranslationService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
@AllArgsConstructor
public class LanguageSelectMenu {

    private final TranslationServiceProvider translationServiceProvider;

    public StringSelectMenu openLanguageSelectionMenu(ServiceType serviceType, int page) {
        StringSelectMenu.Builder stringSelectMenu = StringSelectMenu.create("translate:language")
                .setPlaceholder("Choose up to 3 languages to translate into");
        stringSelectMenu.setMinValues(1);
        stringSelectMenu.setMaxValues(3);

        switch (serviceType) {
            case GOOGLE ->
                    stringSelectMenu.addOptions(getLanguageOptions(translationServiceProvider.getGoogleTranslationService().getLanguageOptions(), page));
            case OPENAI ->
                    stringSelectMenu.addOptions(getLanguageOptions(translationServiceProvider.getOpenAITranslationService().getLanguageOptions(), page));
            case MICROSOFT, DEEPL ->
                    stringSelectMenu.addOptions(getLanguageOptions(translationServiceProvider.getDeepLTranslationService().getLanguageOptions(), page));
        }

        return stringSelectMenu.build();
    }

    public StringSelectMenu openServiceSelectionMenu() {
        List<SelectOption> options = Lists.newArrayList();

        for (ServiceType type : ServiceType.values()) {
            if (type.getSupportedLanguages() > 0) {
                options.add(SelectOption
                        .of(type.name() + " (" + type.getSupportedLanguages() + ")", type.name())
                        .withDescription(type.getDescription())
                        .withEmoji(Emoji.fromUnicode(type.getEmoji())));
            }
        }

        return StringSelectMenu.create("translate:service")
                .setPlaceholder("Select the service to translate with")
                .setMinValues(1)
                .setMaxValues(1)
                .addOptions(options).build();
    }

    public List<SelectOption> getLanguageOptions(List<SelectOption> languages, int page) {
        List<SelectOption> languageOptions = Lists.newArrayList(languages.subList(page * 25, Math.min(languages.size(), (page + 1) * 25)));

        if (page == 0) {
            SelectOption copy = languageOptions.get(0);
            languageOptions.set(0, SelectOption.of("Service Selection", "service")
                    .withDescription("Currently Available: " +
                            String.join(", ", ServiceType.getServices())).withEmoji(Emoji.fromUnicode("⬅️")));
            languageOptions.add(1, copy);
        } else if (page > 0) {
            SelectOption copy = languageOptions.get(0);
            languageOptions.set(0, SelectOption.of("Previous page", "previous").withDescription("Current page: " + (page + 1)).withEmoji(Emoji.fromUnicode("⬅️")));
            languageOptions.add(1, copy);
        }

        if (languages.size() > (page + 1) * 25) {
            //   SelectOption copy = languageOptions.get(24);
            languageOptions.set(24, SelectOption.of("Next page", "next").withDescription("Current page: " + (page + 1)).withEmoji(Emoji.fromUnicode("➡️")));
        }

        return languageOptions.subList(0, Math.min(languageOptions.size(), 25));
    }

}
