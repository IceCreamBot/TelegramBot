package ru.home.fiirst_bot;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.home.fiirst_bot.Admin.GetAdminStrings;
import ru.home.fiirst_bot.DataBase.ConnectionDB;
import ru.home.fiirst_bot.Keyboards.Keyboards;
import ru.home.fiirst_bot.Sandler.Sandler;
import java.util.ArrayList;
import java.util.List;

@Setter
public class Handler {
    String urlPhotoDedault = "https://e7.pngegg.com/pngimages/278/519/png-clipart-computer-icons-question-mark-symbol-question-mark-face-miscellaneous-text.png";
    ConnectionDB connectionDB;
    private MyFirstTelegramBot myFirstTelegramBot;
    private Update update;
    Message message;
    long chatId;
    boolean isAdm;
    String[] text;
    Sandler sandler;

    public Handler(MyFirstTelegramBot myFirstTelegramBot, Update update) {
        this.myFirstTelegramBot = myFirstTelegramBot;
        this.update = update;
        this.connectionDB = new ConnectionDB(urlPhotoDedault);
        handle();
    }

    public void handle() {
        if (update.getMessage() != null && update.getMessage().hasText()) {
            this.message = update.getMessage();
            this.chatId = message.getChatId();
            this.text =  message.getText().split("\\.", 5);
            this.isAdm = myFirstTelegramBot.getChatAdminId() == chatId;
            this.sandler = new Sandler(chatId);

            if(!isAdm){
                defaultHandlerForBuyer();
                }

            else {
                switch (text[0]) {
                    case "добавить":
                        doSendText(connectionDB.create(text));
                        break;
                    case "добавить категорию":
                        doSendText(connectionDB.createCategory(text));
                        break;
                    case "изменить цену":
                        doSendText(connectionDB.update(text));
                        break;
                    case "изменить колличество":
                        doSendText(connectionDB.update(text));
                        break;
                    case "изменить фото":
                        text = message.getText().split("\\.", 3);
                        doSendText(connectionDB.update(text));
                        break;
                    case "удалить":
                        doSendText(connectionDB.delete(text));
                        break;
                    case "удалить категорию":
                        doSendText(connectionDB.deleteCategory(text));
                        break;
                    case "/admin":
                        doSendText(GetAdminStrings.getInfoString());
                        break;
                    default:
                        defaultHandlerForBuyer();
                }
            }
        }
        if (update.hasCallbackQuery()) {
            String chatId = String.valueOf(update.getCallbackQuery().getFrom().getId());
            this.sandler = new Sandler(Long.parseLong(chatId));
            if(update.getCallbackQuery().getData().split("\\.").length == 2) {
                connectionDB.initBasket(chatId);

                if(!connectionDB.isOrder(chatId)) {
                    connectionDB.updateCountOfProductInBd(update.getCallbackQuery().getData().split("\\."), chatId);
                    getBasketInfoOnCallBAck(chatId);
                }
                else{
                    sendTextOnCallBAck("Невозможно редактировать если заказ активен", chatId);
                }
            }
            else {
                if(Long.parseLong(chatId) == myFirstTelegramBot.getChatAdminId()){
                    String answer = update.getCallbackQuery().getData();
                    if(!answer.equals("-")){
                        connectionDB.updateBasketAfterBuy(answer);
                        sendTextOnCallBAck("Успешно", chatId);
                    }
                }
            }
        }

        connectionDB.closeConnection();
    }

    public void defaultHandlerForBuyer(){
        if (connectionDB.equalsWithCategories(message.getText()))
            doSendProducts(message.getText());
        else {
            switch (text[0]) {
                case "Сбросить корзину":
                    if(!connectionDB.isOrder(String.valueOf(chatId))) {
                        connectionDB.resetBasket(String.valueOf(chatId));
                        doSendText("Успешно");
                    }
                    else{
                        doSendText("Невозможно редактировать если заказ активен", String.valueOf(chatId));
                    }
                    break;
                case  "Купить":
                    if(!connectionDB.isOrder(String.valueOf(chatId))) {
                        String orderString = connectionDB.getBasketInfo(String.valueOf(chatId));
                        if(!orderString.equals("ВСЕГО 0 руб.")) {
                            connectionDB.setOrder(String.valueOf(chatId), true);
                            doSendText(GetAdminStrings.getBuyString(orderString, String.valueOf(chatId)));
                            doSendAdminTextWhenBuy(GetAdminStrings.getBuyStringForAdmin(connectionDB, String.valueOf(myFirstTelegramBot.getChatAdminId()), String.valueOf(chatId)),
                                    String.valueOf(chatId));
                        }
                    }
                    else{
                            doSendText("Невозможно редактировать если заказ активен", String.valueOf(chatId));
                        }
                    break;
                case "Убрать заказ":
                    if(!connectionDB.isOrder(String.valueOf(chatId)))doSendText("Заказ не активен");
                    else {
                        doSendText(chatId + " отказался от заказа", String.valueOf(myFirstTelegramBot.getChatAdminId()));
                        connectionDB.setOrder(String.valueOf(chatId), false);
                        connectionDB.resetBasket(String.valueOf(chatId));
                        doSendText("Успешно");
                    }

                default:
                doSendText("Выберите категорию");
            }
        }
    }

    public void doSendProducts(String s) {
            ArrayList<Object[]> arrayList = connectionDB.read(s);
            for (Object[] o : arrayList) {
                doSendPhoto((String) o[3]);
                String ourString = o[0] + "\nЦена: " + o[2] + "\nКолличество: " + o[1];
                doSendProductDescription(ourString, (String) o[0]);
            }
    }

    public void doSendProductDescription(String text, String productName){
        try {
            myFirstTelegramBot.execute(sandler.
                    getTextMessage(text).
                    setReplyMarkup(Keyboards.getInlineKeyboardMarkupForProducts(productName)));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void doSendAdminTextWhenBuy(String text, String chatId){
        try {
            myFirstTelegramBot.execute(sandler.
                    getTextMessage(text).
                    setReplyMarkup(Keyboards.getInlineKeyboardMarkupForAdminWhenBuy(chatId)));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public void doSendText(String text){
        try {
            myFirstTelegramBot.execute(sandler.
                    getTextMessage(text).
                    setReplyMarkup(Keyboards.getMenuKeyboard()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void doSendText(String text, String chatId){
        try {
            myFirstTelegramBot.execute(new Sandler(Long.valueOf(chatId)).
                    getTextMessage(text).
                    setReplyMarkup(Keyboards.getMenuKeyboard()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void doSendPhoto(String url){
        try {
            myFirstTelegramBot.execute(sandler.
                    getPhotoMessage(url));
        } catch (TelegramApiException e) {
            try {
                myFirstTelegramBot.execute(sandler.
                        getPhotoMessage(urlPhotoDedault));
            } catch (TelegramApiException telegramApiException) {
                telegramApiException.printStackTrace();
            }
        }
    }

    public void getBasketInfoOnCallBAck(String chatId){
        try {
            myFirstTelegramBot.execute(new AnswerCallbackQuery().
                    setText(connectionDB.getBasketInfo(chatId)).
                    setShowAlert(false).
                    setCallbackQueryId(update.getCallbackQuery().getId()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendTextOnCallBAck(String text, String chatId){
        try {
            myFirstTelegramBot.execute(new AnswerCallbackQuery().
                    setText(text).
                    setShowAlert(false).
                    setCallbackQueryId(update.getCallbackQuery().getId()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
