package dev.charon.discord.listener;

import com.google.common.base.CharMatcher;
import dev.charon.discord.bot.TranslatorBot;
import dev.charon.discord.check.MessageCheck;
import dev.charon.discord.translation.TranslateMessage;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class ContextMenuListener extends ListenerAdapter {

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("Translate Text")) {
            String text = event.getTarget().getContentRaw();

            if (MessageCheck.check(event, text)) return;

            SelectionInteractListener selectionInteractListener = TranslatorBot.getInstance().getSelectionInteractListener();
            TranslateMessage translateMessage = selectionInteractListener.getTranslateMessages()
                    .stream()
                    .filter(translateMessage1 -> translateMessage1.getUser() == event.getUser())
                    .findFirst()
                    .orElse(new TranslateMessage(event.getUser(),
                            Objects.requireNonNull(event.getGuild()).getId(),
                            event.getChannelId(),
                            text,
                            event.getTarget().getId()));


            if (!selectionInteractListener.getTranslateMessages().contains(translateMessage))
                selectionInteractListener.getTranslateMessages().add(translateMessage);

            translateMessage.setText(text);

            event.reply("Please select the service to translate with!")
                    .addActionRow(selectionInteractListener.getLanguageSelectMenu().openServiceSelectionMenu())
                    .setEphemeral(true).queue();
        }
        super.onMessageContextInteraction(event);
    }
}
