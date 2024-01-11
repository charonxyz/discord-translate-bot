package dev.charon.discord.command.map;

import com.google.common.collect.Lists;
import dev.charon.discord.command.impl.Command;
import lombok.Getter;

import java.util.List;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public class CommandMap {

    private final List<Command> commands = Lists.newArrayList();
    private final List<Command> subcommands = Lists.newArrayList();

}
