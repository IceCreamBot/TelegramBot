package ru.home.fiirst_bot;

import lombok.Setter;

import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;


@Setter
public class MyFirstTelegramBot extends TelegramWebhookBot {
    private String botUserName;
    private String webHookPath;
    private String botToken;
    private long chatAdminId;

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        new Handler(this, update);
        return null;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotPath() {
        return webHookPath;
    }
    public long getChatAdminId() {
        return chatAdminId;
    }
}
