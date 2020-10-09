package ru.home.fiirst_bot.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.home.fiirst_bot.MyFirstTelegramBot;

@RestController
public class WebHookController {
    private final MyFirstTelegramBot myFirstTelegramBot;

    public WebHookController(MyFirstTelegramBot myFirstTelegramBot) {
        this.myFirstTelegramBot = myFirstTelegramBot;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update){
        return myFirstTelegramBot.onWebhookUpdateReceived(update);
    }
}
