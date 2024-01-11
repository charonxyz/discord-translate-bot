package dev.charon.discord.command.loader;

import dev.charon.discord.bot.TranslatorBot;
import dev.charon.discord.command.impl.Command;
import dev.charon.discord.command.impl.CommandImplementation;
import dev.charon.discord.command.map.CommandMap;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.reflections.Reflections;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class CommandLoader {

    private ShardManager jda;
    private CommandMap commandMap;


    public CommandLoader(ShardManager jda, CommandMap commandMap) {
        this.jda = jda;
        this.commandMap = commandMap;
    }

    public void loadCommands() {
        var reflections = new Reflections("dev.charon.discord.command");

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(CommandImplementation.class);

        if (classes.size() == 0) {
            TranslatorBot.LOGGER.info("No commands were found!");
            return;
        }

        for (Class<?> clazz : classes) {
            CommandImplementation command = clazz.getAnnotation(CommandImplementation.class);

            try {

                Command clazzCommand = (Command) clazz.getConstructor(String.class).newInstance(command.name());

                CommandDataImpl commandData = new CommandDataImpl(command.name(), command.description());

                if (clazzCommand.getOptionData() != null) {
                    for (OptionData optionData : clazzCommand.getOptionData()) {
                        if (!commandData.getOptions().contains(optionData)) {
                            commandData.addOptions(optionData);
                        }
                    }
                }

                if (clazzCommand.getSubCommands() != null) {
                    if (clazzCommand.getSubCommands().length < 1) {
                        return;
                    }

                    for (var subCommandClazz : clazzCommand.getSubCommands()) {
                        SubcommandData subcommandData = new SubcommandData(subCommandClazz.getName(), subCommandClazz.getDescription());
                        if (subCommandClazz.getOptionData() != null) {
                            for (OptionData optionData : subCommandClazz.getOptionData()) {
                                if (!subcommandData.getOptions().contains(optionData)) {
                                    subcommandData.addOptions(optionData);
                                    TranslatorBot.LOGGER.info("Added option " + optionData.getName() + " to subcommand " + subCommandClazz.getName());
                                }
                            }
                        }
                        if (!commandData.getSubcommands().contains(subcommandData)) {
                            try {
                                commandData.addSubcommands(subcommandData);
                                TranslatorBot.LOGGER.info("Added subcommand " + subCommandClazz.getName() + " to command " + clazz.getSimpleName());
                            } catch (IllegalArgumentException e) {
                            }
                        }
                    }
                }

                TranslatorBot.LOGGER.info("Command " + command.name() + " was loaded!");

                jda.getGuilds().forEach(guild -> guild.upsertCommand(commandData).queue());

                if (clazz.getAnnotation(CommandImplementation.class) != null) {
                    commandMap.getCommands().add(clazzCommand);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        jda.getGuilds().forEach(guild -> guild.upsertCommand(Commands.message("Translate Text")).queue());
    }

}
