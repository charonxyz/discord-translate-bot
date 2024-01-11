package dev.charon.discord.translation.service.openai;

import com.google.api.client.util.Lists;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import dev.charon.discord.bot.TranslatorBot;
import dev.charon.discord.translation.service.TranslationService;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public class OpenAITranslationService implements TranslationService {

    private final OpenAiService openAiService;

    public OpenAITranslationService(String authKey) {
        this.openAiService = new OpenAiService(authKey);
    }

    @Override
    public String translate(@NotNull String text, @Nullable String sourceLanguage, @NotNull List<String> targetLanguages) {
        List<ChatMessage> chatMessages = getChatMessages(text,
                "According to the following text I want you to translate it into the languages with the language-code " + String.join(", ", targetLanguages) +
                        "Please do only respond with the translated text but with the language-code before it with \":\" for example \"de: Hallo\"\n");

        ChatCompletionRequest completionRequest = buildChatCompletionRequest(chatMessages);

        AtomicReference<String> translatedText = new AtomicReference<>("");
        openAiService.createChatCompletion(completionRequest).getChoices().forEach(
                choice -> translatedText.set(choice.getMessage().getContent())
        );
        return translatedText.get();
    }

    @Override
    public String getSourceLanguage(@NotNull String text) {
        List<ChatMessage> chatMessages = getChatMessages(text,
                "According to the following text I want you to determine the language that has been given. " +
                        "Please do only respond with the lang-Code in a format of \"de\" or \"en-US\". " +
                        "Do not include anything else other than detecting the language code.\n");

        ChatCompletionRequest completionRequest = buildChatCompletionRequest(chatMessages);

        AtomicReference<String> language = new AtomicReference<>("");
        openAiService.createChatCompletion(completionRequest).getChoices().forEach(
                choice -> language.set(choice.getMessage().getContent())
        );
        return language.get();
    }

    @NotNull
    private static List<ChatMessage> getChatMessages(@NotNull String text, String content) {
        List<ChatMessage> chatMessages = Lists.newArrayList();
        ChatMessage system = new ChatMessage(ChatMessageRole.SYSTEM.value(),
                content);
        ChatMessage user = new ChatMessage(ChatMessageRole.USER.value(), text);
        chatMessages.add(system);
        chatMessages.add(user);
        return chatMessages;
    }

    private ChatCompletionRequest buildChatCompletionRequest(List<ChatMessage> chatMessages) {
        return ChatCompletionRequest.builder()
                .messages(chatMessages)
                .temperature(0.9)
                .frequencyPenalty(0.0)
                .presencePenalty(0.6)
                .maxTokens(64)
                .model("gpt-3.5-turbo-0613")
                .build();
    }
}
