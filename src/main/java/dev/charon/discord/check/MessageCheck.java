package dev.charon.discord.check;

import com.google.common.base.CharMatcher;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class MessageCheck {

    public static boolean isMessageValid(String message) {
        return message.length() >= 2 && message.length() < 2000;
    }

    public static boolean isMessageAscii(String message) {
        return message.matches("[0-9aA-zZ\\s!?,.#;:'%/+-äÄöÖüÜß]+");
    }

    public static boolean isMessageOnlyNumbers(String message) {
        return message.matches("[0-9]+") && message.length() > 2;
    }

    public static boolean isMessageOnlyRepeatingCharacters(String message) {
        return message.matches("([a-zA-Z])\\1*");
    }

    public static boolean check(IReplyCallback event, String text) {
        /*if (!MessageCheck.isMessageAscii(text)) {
            event.reply("Text is not ASCII! Please select a text that is ASCII!")
                    .setEphemeral(true).queue();
            return true;
        }*/
        if (MessageCheck.isMessageOnlyNumbers(text)) {
            event.reply("Text is only numbers! Please select a text that is not only numbers!")
                    .setEphemeral(true).queue();
            return true;
        }
        if (MessageCheck.isMessageOnlyRepeatingCharacters(text)) {
            event.reply("Text is only repeating characters! Please select a text that is not only repeating characters!")
                    .setEphemeral(true).queue();
            return true;
        }
        if (!MessageCheck.isMessageValid(text)) {
            event.reply("Text is too short or too long! Please select a text that is more than 1 character and smaller than 2'000 characters!")
                    .setEphemeral(true).queue();
            return true;
        }
        return false;
    }
}
