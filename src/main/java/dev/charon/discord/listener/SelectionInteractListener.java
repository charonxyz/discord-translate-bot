package dev.charon.discord.listener;

import com.google.api.client.util.Lists;
import dev.charon.discord.selectmenu.LanguageSelectMenu;
import dev.charon.discord.translation.TranslateMessage;
import dev.charon.discord.translation.TranslationServiceProvider;
import dev.charon.discord.translation.service.ServiceType;
import dev.charon.discord.translation.service.TranslationService;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Setter
@Getter
public class SelectionInteractListener extends ListenerAdapter {

    private List<TranslateMessage> translateMessages = Lists.newArrayList();
    private final TranslationServiceProvider translationServiceProvider;

    private final LanguageSelectMenu languageSelectMenu;

    public SelectionInteractListener(TranslationServiceProvider translationServiceProvider) {
        this.translationServiceProvider = translationServiceProvider;
        this.languageSelectMenu = new LanguageSelectMenu(translationServiceProvider);
    }

    // ServiceSelection Menu Logic
    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        if (event.getComponent().getId().equalsIgnoreCase("translate:service")) {
            if (event.getGuild() == null || translateMessages.isEmpty()) return;

            StringSelectInteraction interaction = event.getInteraction();

            if (interaction.getValues().isEmpty()) {
                event.editMessage("Please select a service!").queue();
                return;
            }

            TranslateMessage translateMessage = translateMessages.stream().filter(message -> message.getUser().getIdLong() == event.getUser().getIdLong()).findFirst().orElse(null);
            if (translateMessage == null) {
                event.editMessage("An error occurred while translating! Message not found").queue();
                return;
            }

            if (guildOrChannelNotEquals(translateMessage, event)) return;

            String service = interaction.getValues().get(0);
            translateMessage.setTranslationService(getService(service));
            ServiceType serviceType = ServiceType.fromString(service.toUpperCase());

            if (serviceType == null) {
                event.editMessage("An error occurred while translating! Service not found").queue();
                return;
            }

            translateMessage.setServiceType(serviceType);

            event.editMessage("Selected service: " + serviceType + " (" + serviceType.getSupportedLanguages() + ")\nChoose up to 3 languages to translate into!")
                    .setActionRow(languageSelectMenu.openLanguageSelectionMenu(Objects.requireNonNull(ServiceType.fromString(service)), translateMessage.getPage()))
                    .queue();
        }

        if (event.getComponent().getId().equalsIgnoreCase("translate:language")) {
            if (event.getGuild() == null || translateMessages == null) return;

            long messageId = event.getMessageIdLong();

            event.deferEdit().queue();
            StringSelectInteraction interaction = event.getInteraction();

            if (interaction.getValues().isEmpty()) {
                event.getHook().editMessageById(messageId, "Please select a language!").queue();
                return;
            }

            List<String> languages = interaction.getValues();
            TranslateMessage translateMessage = translateMessages.stream().filter(message -> message.getUser().getIdLong() == event.getUser().getIdLong()).findFirst().orElse(null);

            if (translateMessage == null) {
                event.getHook().editMessageById(messageId, "An error occurred while translating! (Message is null)").queue();
                return;
            }

            if (guildOrChannelNotEquals(translateMessage, event)) return;

            if (languages.contains("service")) {
                event.getHook().editMessageById(messageId, "Select the service to translate with!").setActionRow(languageSelectMenu.openServiceSelectionMenu()).queue();
                return;
            }

            if (languages.contains("previous") && languages.contains("next")) {
                event.getHook().editMessageById(messageId, "You can't select previous and next at the same time!").queue();
                return;
            }

            if (languages.contains("previous")) {
                if (translateMessage.getPage() == 0) {
                    event.getHook().editMessageById(messageId, "You can't go further than page 0!").queue();
                    return;
                }
                translateMessage.setPage(translateMessage.getPage() - 1);
                event.getHook().editMessageById(messageId,
                                "Selected service: " + translateMessage.getServiceType() + " (" + translateMessage.getServiceType().getSupportedLanguages() + ")\nChoose up to 3 languages to translate into!")
                        .setActionRow(languageSelectMenu.openLanguageSelectionMenu(translateMessage.getServiceType(), translateMessage.getPage()))
                        .queue();
                return;
            } else if (languages.contains("next")) {
                if (translateMessage.getPage() == 2) {
                    event.getHook().editMessageById(messageId, "You can't go further than page 2!").queue();
                    return;
                }
                ServiceType serviceType = translateMessage.getServiceType();
                translateMessage.setPage(translateMessage.getPage() + 1);
                event.getHook().editMessageById(messageId, "Selected service: " + serviceType + " (" + serviceType.getSupportedLanguages() + ")" +
                                "\nChoose up to 3 languages to translate into!")
                        .setActionRow(languageSelectMenu.openLanguageSelectionMenu(translateMessage.getServiceType(), translateMessage.getPage()))
                        .queue();
                return;
            }

            String sourceLanguage = translateMessage.getTranslationService().getSourceLanguage(translateMessage.getText());
            if (sourceLanguage != null && languages.contains(sourceLanguage)) {
                event.getHook().editMessageById(messageId, "The source language is already in the given languages! (" + sourceLanguage + ").").queue();
                return;
            }

            String translatedText = translateMessage.getTranslationService().translate(translateMessage.getText(), sourceLanguage, languages);

            if (translatedText == null) {
                event.getHook().editMessageById(messageId, "An error occurred while translating! (Text is null)").queue();
                return;
            }
            if (translatedText.equalsIgnoreCase(translateMessage.getText())) {
                event.getHook().editMessageById(messageId, "The text is already in the given language!").queue();
                return;
            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Original ")
                    .append(sourceLanguage != null ? "(" + sourceLanguage + ")" : "")
                    .append(": ")
                    .append(translateMessage.getText())
                    .append("\n")
                    .append(translatedText).append("\n");

            if (stringBuilder.length() > 2000) {
                event.getHook().editMessageById(messageId, "Translated text is too long!").queue();
                return;
            }

            event.getHook().editMessageById(messageId, stringBuilder.toString()).setActionRow(
                    Button.danger("translate:selfdestruct", "Warning: This message will self-destruct in 45 seconds").withDisabled(true)).queue();
            translateMessages.remove(translateMessage);
            event.getHook().deleteMessageById(messageId).queueAfter(45, TimeUnit.SECONDS);
        }
        super.onStringSelectInteraction(event);
    }

    private TranslationService getService(String service) {
        TranslationService translateService = getTranslationServiceProvider().getDeepLTranslationService(); // default
        switch (service.toLowerCase()) {
            //     case "microsoft" -> translateService = getTranslationServiceProvider().getMicrosoftTranslationService(); > not implemented yet
            case "openai" -> translateService = getTranslationServiceProvider().getOpenAITranslationService();
            case "google" -> translateService = getTranslationServiceProvider().getGoogleTranslationService();
        }
        return translateService;
    }

    private boolean guildOrChannelNotEquals(TranslateMessage translateMessage, StringSelectInteractionEvent event) {
        if (event.getGuild() == null || event.getGuild().getGuildChannelById(translateMessage.getChannelId()) == null) {
            event.editMessage("An error occurred while translating! (Guild or Channel is null)").queue();
            translateMessages.remove(translateMessage);
            return true;
        }
        if (!event.getGuild().getId().equals(translateMessage.getGuildId())) {
            event.editMessage("An error occurred while translating! (Guild ID is not the same with requested translation)").queue();
            translateMessages.remove(translateMessage);
            return true;
        }
        if (!event.getGuild().getGuildChannelById(translateMessage.getChannelId()).getId().equals(translateMessage.getChannelId())) {
            event.editMessage("An error occurred while translating! (Channel ID is not the same with requested translation)").queue();
            translateMessages.remove(translateMessage);
            return true;
        }
        return false;
    }

}
