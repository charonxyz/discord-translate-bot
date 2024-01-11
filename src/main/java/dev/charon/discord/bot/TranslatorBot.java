package dev.charon.discord.bot;

import com.google.gson.*;
import dev.charon.discord.command.event.CommandEvent;
import dev.charon.discord.command.loader.CommandLoader;
import dev.charon.discord.command.map.CommandMap;
import dev.charon.discord.configuarion.BotConfiguration;
import dev.charon.discord.language.UnicodeLoader;
import dev.charon.discord.listener.ContextMenuListener;
import dev.charon.discord.listener.SelectionInteractListener;
import dev.charon.discord.translation.TranslationServiceProvider;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public class TranslatorBot {

    @Getter
    private static TranslatorBot instance;
    public static final Logger LOGGER = Logger.getLogger("TranslatorBot");

    private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
    private final File file = new File("bot");

    private ShardManager jda;

    private TranslationServiceProvider translationService;

    private UnicodeLoader unicodeLoader;

    private CommandMap commandMap;
    private SelectionInteractListener selectionInteractListener;

    public static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(10);

    public TranslatorBot() {
        instance = this;

        onEnable();
    }

    private void onEnable() {
        BotConfiguration botConfiguration = loadConfiguration();

        DefaultShardManagerBuilder shardManagerBuilder = DefaultShardManagerBuilder.createDefault(botConfiguration.getToken());
        configureMemoryUsage(shardManagerBuilder);
        this.jda = shardManagerBuilder.build();
        this.jda.setActivity(Activity.customStatus("/translate or use me as a app"));

        loadServices(botConfiguration);

        this.commandMap = new CommandMap();
        CommandLoader commandLoader = new CommandLoader(jda, commandMap);
        commandLoader.loadCommands();

        loadListeners();

        Runtime.getRuntime().addShutdownHook(new Thread(this::onDisable));
    }

    public void onDisable() {
        LOGGER.info("Exiting...");
        jda.shutdown();
        EXECUTOR_SERVICE.shutdown();
        LOGGER.info("Shutdown complete! Bye!");
    }


    private void loadServices(BotConfiguration botConfiguration) {
        unicodeLoader = new UnicodeLoader(this);
        unicodeLoader.loadUnicodeProvider(file);
        unicodeLoader.loadFlags();

        if (unicodeLoader.getUnicodeProvider() == null) {
            LOGGER.severe("UnicodeProvider is null!");
            System.exit(0);
            return;
        }

        translationService = new TranslationServiceProvider(botConfiguration);
    }

    private void loadListeners() {
        this.selectionInteractListener = new SelectionInteractListener(translationService);
        this.jda.addEventListener(new CommandEvent());
        this.jda.addEventListener(selectionInteractListener);
        this.jda.addEventListener(new ContextMenuListener());
    }

    private void configureMemoryUsage(DefaultShardManagerBuilder jdaBuilder) {
        jdaBuilder.disableCache(
                CacheFlag.ACTIVITY,
                CacheFlag.VOICE_STATE,
                CacheFlag.CLIENT_STATUS,
                CacheFlag.SCHEDULED_EVENTS,
                CacheFlag.STICKER);

        jdaBuilder.enableIntents(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MESSAGE_TYPING);

       // jdaBuilder.setActivity(Activity.playing("with multiple services..."));
    }

    @SneakyThrows
    private BotConfiguration loadConfiguration() {

        if (file.mkdirs()) {
            LOGGER.info("Created bot folder");
        }

        final Path path = Paths.get("bot/config.json");
        if (!Files.exists(path)) {
            Files.copy(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("config.json")), path);
            LOGGER.info("Created config.json");
        }

        final BotConfiguration botConfiguration = gson.fromJson(Files.newBufferedReader(path), BotConfiguration.class);
        if (botConfiguration == null) {
            LOGGER.severe("Failed to load config.json");
            System.exit(0);
        }
        return botConfiguration;
    }

    public String getEmoji(String countryCode) {
        if (unicodeLoader.getUnicodeProvider().getUnicode().isEmpty()) {
            System.out.println("UnicodeProvider is empty!");
        }
        if (unicodeLoader.getUnicodeProvider().getUnicode().containsKey(countryCode.toUpperCase())) {
            return unicodeLoader.getUnicodeProvider().getUnicode().get(countryCode.toUpperCase());
        }

        return null;
    }

}
