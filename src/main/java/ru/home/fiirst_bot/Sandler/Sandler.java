package ru.home.fiirst_bot.Sandler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;


public class Sandler {
    private long chatId;

    public Sandler(long chatId) {
        this.chatId = chatId;
    }

    public SendMessage getTextMessage(String text) {
        return new SendMessage().setChatId(chatId).setText(text);
    }

    public SendMessage getTextMessage(String text, String chatId) {
        return new SendMessage().setChatId(chatId).setText(text);
    }

    public SendPhoto getPhotoMessage(String url){
        return  new SendPhoto().setChatId(chatId).setPhoto(url);
    }

}
