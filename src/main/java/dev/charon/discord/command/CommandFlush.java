package dev.charon.discord.command;

import dev.charon.discord.bot.TranslatorBot;
import dev.charon.discord.command.impl.Command;
import dev.charon.discord.command.impl.CommandImplementation;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@CommandImplementation(name = "flush", description = "Flush all commands")
public class CommandFlush extends Command {

    public CommandFlush(String name) {
        super(name);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, MessageChannelUnion channel) {
        var user = event.getUser();

        if (user.getId().equals("858292829481467905")) {
            TranslatorBot.getInstance().getJda().getGuilds().forEach(guild -> guild.updateCommands().queue());
            event.reply("Flushed all the commands!").setEphemeral(true).queue();
        } else {
            event.reply("Das kannst du nicht!").setEphemeral(true).queue();
        }
    }
}
