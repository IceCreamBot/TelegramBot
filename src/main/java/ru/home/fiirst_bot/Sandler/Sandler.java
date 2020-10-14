package ru.home.fiirst_bot.Sandler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.home.fiirst_bot.DataBase.ConnectionDB;
import ru.home.fiirst_bot.MyFirstTelegramBot;

import javax.swing.text.TableView;


public class Sandler {
    private long chatId;

    public Sandler(long chatId) {
        this.chatId = chatId;
    }

    public SendMessage getTextMessage(String text) {
        return new SendMessage().setChatId(chatId).setText(text);
    }

    public SendPhoto getPhotoMessage(String url){
        return  new SendPhoto().setChatId(chatId).setPhoto(url);
    }

}
