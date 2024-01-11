package dev.charon.discord.translation;

import dev.charon.discord.translation.service.TranslationService;
import dev.charon.discord.translation.service.ServiceType;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
@Setter
public class TranslateMessage {

    private final User user;
    private final String guildId;
    private final String channelId;

    private int page = 0; // start at page 0
    private String text;
    private String messageId;
    private TranslationService translationService;
    private ServiceType serviceType;

    public TranslateMessage(User user, String guildId, String channelId, String text) {
        this.user = user;
        this.guildId = guildId;
        this.channelId = channelId;
        this.text = text;
    }
    public TranslateMessage(User user, String guildId, String channelId, String text, String messageId) {
        this.user = user;
        this.guildId = guildId;
        this.channelId = channelId;
        this.text = text;
        this.messageId = messageId;
    }
}
