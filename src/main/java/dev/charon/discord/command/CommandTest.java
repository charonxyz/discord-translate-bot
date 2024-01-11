package dev.charon.discord.command;

import dev.charon.discord.command.impl.Command;
import dev.charon.discord.command.impl.CommandImplementation;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@CommandImplementation(name = "test", description = "Test command")
public class CommandTest extends Command {

    public CommandTest(String name) {
        super(name);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, MessageChannelUnion channel) {
        event.reply("Test command!").setEphemeral(true)
                .addActionRow(Button.success("test", "Klick mich")).queue();
    }
}
