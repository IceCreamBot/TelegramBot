package ru.home.fiirst_bot.appconfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import ru.home.fiirst_bot.MyFirstTelegramBot;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "telegrambot")
public class BotConfig {
    private String webHookPath;
    private String botUserName;
    private String botToken;
    private long chatAdminId;

    @Bean
    public MyFirstTelegramBot myFirstTelegramBot(){
        MyFirstTelegramBot myFirstTelegramBot = new MyFirstTelegramBot();

        myFirstTelegramBot.setWebHookPath(webHookPath);
        myFirstTelegramBot.setBotToken(botToken);
        myFirstTelegramBot.setBotUserName(botUserName);
        myFirstTelegramBot.setChatAdminId(chatAdminId);

        return myFirstTelegramBot;
    }
}