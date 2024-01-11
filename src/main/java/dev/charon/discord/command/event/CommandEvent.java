package dev.charon.discord.command.event;

import dev.charon.discord.bot.TranslatorBot;
import dev.charon.discord.command.impl.Command;
import dev.charon.discord.command.map.CommandMap;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class CommandEvent extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        CommandMap commandMap = TranslatorBot.getInstance().getCommandMap();

        if (commandMap.getCommands().isEmpty()) {
            TranslatorBot.LOGGER.info("No commands were found!");
            return;
        }

        for (Command command : commandMap.getCommands()) {
            if (event.getName().equalsIgnoreCase(command.getName())) {
                command.execute(event, event.getChannel());
                return;
            }
        }
        super.onSlashCommandInteraction(event);
    }
}
