package dev.charon.discord.command.impl;

import lombok.Getter;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public abstract class Command {

    private final String name;
    private OptionData[] optionData;
    private SubCommand[] subCommands;

    public Command(String name) {
        this.name = name;
    }

    public Command(String name, SubCommand... subCommands) {
        this.name = name;
        this.subCommands = subCommands;
    }

    public Command(String name, OptionData... optionData) {
        this.name = name;
        this.optionData = optionData;
    }

    public Command(String name, SubCommand[] subCommands, OptionData... optionData) {
        this.name = name;
        this.optionData = optionData;
        this.subCommands = subCommands;
    }

    public abstract void execute(SlashCommandInteractionEvent event, MessageChannelUnion channel);

}
