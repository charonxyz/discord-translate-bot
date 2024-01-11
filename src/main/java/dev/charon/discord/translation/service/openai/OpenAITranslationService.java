package dev.charon.discord.translation.service.openai;

import com.google.api.client.util.Lists;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import dev.charon.discord.bot.TranslatorBot;
import dev.charon.discord.translation.service.TranslationService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
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
    private final List<SelectOption> languageOptions = Lists.newArrayList();

    public OpenAITranslationService(String authKey) {
        this.openAiService = new OpenAiService(authKey);

        loadSupportedLanguages();

    }

    @Override
    public String translate(@NotNull String text, @Nullable String sourceLanguage, @NotNull List<String> targetLanguages) {
        List<ChatMessage> chatMessages = getChatMessages(text,
                "According to the following text I want you to translate it into the languages with the language-code " + String.join(", ", targetLanguages) +
                        "Please do only respond with the translated text but with the language-code before it with \":\" for example \"de: Hallo\"\n");

        ChatCompletionRequest completionRequest = buildChatCompletionRequest(chatMessages, 64);

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

        ChatCompletionRequest completionRequest = buildChatCompletionRequest(chatMessages, 64);

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

    private ChatCompletionRequest buildChatCompletionRequest(List<ChatMessage> chatMessages, int tokens) {
        return ChatCompletionRequest.builder()
                .messages(chatMessages)
                .temperature(0.9)
                .frequencyPenalty(0.0)
                .presencePenalty(0.6)
                .maxTokens(tokens)
                .model("gpt-3.5-turbo-0613")
                .build();
    }

    private void loadSupportedLanguages() {
        List<ChatMessage> chatMessages = getChatMessages(
                """
                        Which languages can you translate without problems?\s
                        Give me at least 15 languages. Provide it as "languagename, languagecode, country\"""",
                """
                        Please just respond with the language code in a format of {"German, Germany, de"} or {"United States, English, GB"} and nothing else.
                        Dont add dialects or anything else. Just the common languages.
                        Add the languagename always first, then the languagecode and then the country. ALWAYS!
                        """);

        ChatCompletionRequest completionRequest = buildChatCompletionRequest(chatMessages, 128);
        AtomicReference<String> languages = new AtomicReference<>("");
        openAiService.createChatCompletion(completionRequest).getChoices().forEach(
                choice -> languages.set(choice.getMessage().getContent())
        );

        List<Language> languageList = Lists.newArrayList();
        Arrays.stream(languages.get().split("\n")).forEach(
                language -> {
                    String[] split = language.split(",");
                    languageList.add(new Language(split[0].trim(), split[1].trim(), split[2].trim()));
                }
        );

        for (Language language : languageList) {
            String code = language.getCode();

            if (code.equalsIgnoreCase("en")) {
                code = "GB";
            }
            String formattedCode = TranslatorBot.getInstance().getEmoji(code.toUpperCase());
            if (formattedCode == null) {
                TranslatorBot.LOGGER.severe("Could not find emoji for language: " + language.getName() + " (" + code + ")");
                continue;
            }

            UnicodeEmoji emoji = Emoji.fromUnicode(formattedCode);

            SelectOption option = SelectOption.of(language.getName() + " (" + language.getCode() + ")", language.getCode())
                    .withDescription("Language code: " + language.getCode())
                    .withEmoji(emoji);
            if (languageOptions.contains(option)) {
                continue;
            }

            languageOptions.add(option);
           //System.out.println(language.getName() + " (" + language.getCode().split("-")[0] + ") " + language.getCountry());
        }
    }

    @Getter
    @AllArgsConstructor
    private static final class Language {

        private final String name;
        private final String code;
        private final String country;

    }
}
