package ru.home.fiirst_bot;

import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.home.fiirst_bot.DataBase.ConnectionDB;
import ru.home.fiirst_bot.Keyboards.Keyboards;


import java.sql.SQLException;
import java.util.ArrayList;

@Setter
public class Handler {
    private MyFirstTelegramBot myFirstTelegramBot;
    private Update update;
    private ConnectionDB connection;

    public Handler(MyFirstTelegramBot myFirstTelegramBot, Update update) {
        this.myFirstTelegramBot = myFirstTelegramBot;
        this.update = update;
        handled();
    }

    public void handled() {
        if (update.getMessage() != null && update.getMessage().hasText()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            String[] text = message.getText().split("\\.", 5);


            if (text.length == 1) {
                switch (message.getText()) {
                    case "Напитки":
                        doSendProducts(message.getText(), chatId);
                        break;
                    case "Чипсы":
                        doSendProducts(message.getText(), chatId);
                        break;
                    case "Шоколад":
                        doSendProducts(message.getText(), chatId);
                        break;
                    default:
                        doSendText("Выберите категорию", chatId);
                }
            }

           else if (myFirstTelegramBot.getChatAdminId() == message.getChatId()){
                    switch (text[0]) {
                        case "добавить":
                            connection = new ConnectionDB();
                            try {
                                doSendText(connection.create(text), chatId);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            break;
                        case "добавить категорию":
                            connection = new ConnectionDB();
                            try {
                                doSendText(connection.create(text), chatId);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            break;

                        case "изменить цену":
                            connection = new ConnectionDB();
                            try {
                                doSendText(connection.update(text), chatId);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            break;
                        case "изменить колличество":
                            connection = new ConnectionDB();
                            try {
                                doSendText(connection.update(text), chatId);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            break;
                        case "изменить фото":
                            text = message.getText().split("\\.", 3);
                            connection = new ConnectionDB();
                            try {
                                doSendText(connection.update(text), chatId);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            break;
                        case "удалить":
                            connection = new ConnectionDB();
                            try {
                                doSendText(connection.delete(text), chatId);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            break;
                        case "удалить категорию":
                            connection = new ConnectionDB();
                            try {
                                doSendText(connection.delete(text), chatId);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            break;
                    }
                }
            }
        }

    public void doSendText(String text, long chatId){
        try {
            myFirstTelegramBot.execute(new SendMessage().setChatId(chatId).setReplyMarkup(Keyboards.getMenuKeyboard()).
                    setText(text));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void doSendProducts(String s, long chatId){
        connection = new ConnectionDB();
        try {
            ArrayList<Object[]> arrayList = connection.read(s);
            for(Object[] o: arrayList) {
                try{
                myFirstTelegramBot.execute(new SendPhoto().setChatId(chatId).setReplyMarkup(Keyboards.getContextKeyboard()).
                        setPhoto((String) o[3]));}
                        catch (TelegramApiException e){
                            myFirstTelegramBot.execute(new SendPhoto().setChatId(chatId).setReplyMarkup(Keyboards.getContextKeyboard()).
                                    setPhoto("https://prof-lic.com/upload/medialibrary/5f3/5f30deb314f64899cd1c46a3a4f561c3.png"));
                        }
                String ourString = o[0] + "\nЦена: " + o[2] + "\nКолличество: " + o[1];
                myFirstTelegramBot.execute(new SendMessage().setChatId(chatId).setReplyMarkup(Keyboards.getContextKeyboard()).
                        setText(ourString));
            }
        } catch (SQLException e){
            e.printStackTrace();
            }
         catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
