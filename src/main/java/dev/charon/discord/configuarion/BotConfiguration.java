package dev.charon.discord.configuarion;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
@AllArgsConstructor
public class BotConfiguration {

    private final String token;

    // These are the API keys for the translation services
    private final String deepLAuthKey;
    private final String googleAuthKey;
    private final String microsoftAuthKey;
    private final String openAiAuthKey;
    // Database configuration (not implemented yet)
    private final String address;
    private final int port;
    private final String username;
    private final String password;
    private final String database;

}
