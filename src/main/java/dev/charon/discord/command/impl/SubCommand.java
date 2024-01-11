package dev.charon.discord.command.impl;

import lombok.Getter;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public class SubCommand {

    private final String name;
    private final String description;
    private OptionData[] optionData;

    public SubCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public SubCommand(String name, String description, OptionData... optionData) {
        this.name = name;
        this.description = description;
        this.optionData = optionData;
    }

}
