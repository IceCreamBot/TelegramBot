package ru.home.fiirst_bot;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.home.fiirst_bot.Admin.GetAdminStrings;
import ru.home.fiirst_bot.DataBase.ConnectionDB;
import ru.home.fiirst_bot.Keyboards.Keyboards;
import java.util.ArrayList;

@Setter
public class Handler {
    String urlPhotoDedault = "https://prof-lic.com/upload/medialibrary/5f3/5f30deb314f64899cd1c46a3a4f561c3.png";
    private MyFirstTelegramBot myFirstTelegramBot;
    private Update update;
    private ConnectionDB connection = new ConnectionDB();
    Message message = update.getMessage();
    long chatId = message.getChatId();
    boolean isAdm = myFirstTelegramBot.getChatAdminId() == chatId;
    String[] text = message.getText().split("\\.", 5);

    public Handler(MyFirstTelegramBot myFirstTelegramBot, Update update) {
        this.myFirstTelegramBot = myFirstTelegramBot;
        this.update = update;
        handled();
    }

    public void handled() {
        if (update.getMessage() != null && update.getMessage().hasText()) {
            if (!isAdm) {
                if (connection.equalsWithCategories(message.getText()))
                    doSendProducts(message.getText());
                else {
                    doSendText("Выберите категорию");
                }

            }

            if (isAdm) {
                switch (text[0]) {
                    case "добавить":
                        doSendText(connection.create(text));
                        break;
                    case "добавить категорию":
                        doSendText(connection.createCategory(text));
                        break;
                    case "изменить цену":
                        doSendText(connection.update(text));
                        break;
                    case "изменить колличество":
                        doSendText(connection.update(text));
                        break;
                    case "изменить фото":
                        text = message.getText().split("\\.", 3);
                        doSendText(connection.update(text));
                        break;
                    case "удалить":
                        doSendText(connection.delete(text));
                        break;
                    case "удалить категорию":
                        doSendText(connection.deleteCategory(text));
                        break;
                    case "/admin":
                        doSendText(GetAdminStrings.getInfoString());
                        break;
                }
            }
        }
    }

    public void doSendText(String text) {
        try {
            myFirstTelegramBot.execute(new SendMessage().setChatId(chatId).setReplyMarkup(Keyboards.getMenuKeyboard()).
                    setText(text));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void doSendText(String text, long chatId) {
        try {
            myFirstTelegramBot.execute(new SendMessage().setChatId(chatId).setReplyMarkup(Keyboards.getMenuKeyboard()).
                    setText(text));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void doSendPhoto(String url){
        try {
            myFirstTelegramBot.execute(new SendPhoto().setChatId(chatId).setReplyMarkup(Keyboards.getContextKeyboard()).
                    setPhoto(url));
        } catch (TelegramApiException e) {
            try {
                myFirstTelegramBot.execute(new SendPhoto().setChatId(chatId).setReplyMarkup(Keyboards.getContextKeyboard()).
                        setPhoto(urlPhotoDedault));
            } catch (TelegramApiException telegramApiException) {
                telegramApiException.printStackTrace();
            }
        }
    }

    public void doSendProducts(String s) {
        try {
            ArrayList<Object[]> arrayList = connection.read(s);
            for (Object[] o : arrayList) {
                doSendPhoto((String) o[3]);
                String ourString = o[0] + "\nЦена: " + o[2] + "\nКолличество: " + o[1];
                myFirstTelegramBot.execute(new SendMessage().setChatId(chatId).setReplyMarkup(Keyboards.getContextKeyboard()).
                        setText(ourString));
            }
        }
        catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
