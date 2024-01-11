package dev.charon.discord.command;

import dev.charon.discord.bot.TranslatorBot;
import dev.charon.discord.check.MessageCheck;
import dev.charon.discord.command.impl.Command;
import dev.charon.discord.command.impl.CommandImplementation;
import dev.charon.discord.listener.SelectionInteractListener;
import dev.charon.discord.translation.TranslateMessage;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Objects;


/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@CommandImplementation(name = "translate", description = "Translate a message")
public class CommandTranslate extends Command {

    public CommandTranslate(String name) {
        super(name, new OptionData(OptionType.STRING, "text", "Text to translate", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, MessageChannelUnion channel) {
        if (event.getOptions().isEmpty()) {
            event.reply("Please specify a option!").setEphemeral(true).queue();
            return;
        }

        var option = event.getOption("text");
        if (option != null) {
            String text = event.getOption("text").getAsString();

            if (MessageCheck.check(event, text)) return;

            SelectionInteractListener selectionInteractListener = TranslatorBot.getInstance().getSelectionInteractListener();
            selectionInteractListener.getTranslateMessages().add(new TranslateMessage(
                    event.getUser(),
                    Objects.requireNonNull(event.getGuild()).getId(),
                    event.getChannelId(),
                    text));

            event.reply("Please select the service to translate with!")
                    .addActionRow(selectionInteractListener.getLanguageSelectMenu().openServiceSelectionMenu())
                    .setEphemeral(true).queue();
        }
    }

}
