package ru.home.fiirst_bot;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.home.fiirst_bot.Admin.GetAdminStrings;
import ru.home.fiirst_bot.DataBase.BasketCRUD;
import ru.home.fiirst_bot.DataBase.ConnectionToDB;
import ru.home.fiirst_bot.DataBase.ProductsCRUD;
import ru.home.fiirst_bot.Keyboards.Keyboards;
import ru.home.fiirst_bot.Sandler.Sandler;
import java.util.ArrayList;
import java.util.List;

@Setter
public class Handler {
    String urlPhotoDedault = "https://e7.pngegg.com/pngimages/278/519/png-clipart-computer-icons-question-mark-symbol-question-mark-face-miscellaneous-text.png";
    BasketCRUD basketCRUD;
    ProductsCRUD productsCRUD;
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
        this.productsCRUD = new ProductsCRUD(urlPhotoDedault);
        this.basketCRUD = new BasketCRUD(productsCRUD);
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
                        doSendText(productsCRUD.createProduct(text[1], text[2], text[3], text[4]));
                        break;
                    case "добавить категорию":
                        doSendText(productsCRUD.createCategory(text[1]));
                        break;
                    case "изменить цену":
                        doSendText(productsCRUD.updatePrice(text[1], Integer.parseInt(text[2])));
                        break;
                    case "изменить колличество":
                        doSendText(productsCRUD.updateQuantity(text[1], Integer.parseInt(text[2])));
                        break;
                    case "изменить фото":
                        text = message.getText().split("\\.", 3);
                        doSendText(productsCRUD.updateUrl(text[1], text[2]));
                        break;
                    case "удалить":
                        doSendText(productsCRUD.deleteProduct(text[1]));
                        break;
                    case "удалить категорию":
                        doSendText(productsCRUD.deleteCategory(text[1]));
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
                basketCRUD.initBasket(chatId);

                if(!basketCRUD.isOrder(chatId)) {
                    String[] answer = update.getCallbackQuery().getData().split("\\.");
                    if(answer[1].equals("+")) basketCRUD.plusCountOfProductInBd(answer[0], chatId);
                    else if (answer[1].equals("-")) basketCRUD.minusCountOfProductInBd(answer[0], chatId);
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
                        basketCRUD.updateBasketAfterBuy(answer);
                        sendTextOnCallBAck("Успешно", chatId);
                    }
                }
            }
        }

        basketCRUD.closeConnection();
        productsCRUD.closeConnection();
    }

    public void defaultHandlerForBuyer(){
        if (productsCRUD.equalsWithCategories(message.getText()))
            doSendProducts(message.getText());
        else {
            switch (text[0]) {
                case "Сбросить корзину":
                    if(!basketCRUD.isOrder(String.valueOf(chatId))) {
                        basketCRUD.resetBasket(String.valueOf(chatId));
                        doSendText("Успешно");
                    }
                    else{
                        doSendText("Невозможно редактировать если заказ активен", String.valueOf(chatId));
                    }
                    break;
                case  "Купить":
                    if(!basketCRUD.isOrder(String.valueOf(chatId))) {
                        String orderString = basketCRUD.getBasketInfo(String.valueOf(chatId));
                        if(!orderString.equals("ВСЕГО 0 руб.")) {
                            basketCRUD.setOrder(String.valueOf(chatId), true);
                            doSendText(GetAdminStrings.getBuyString(orderString, String.valueOf(chatId)));
                            doSendAdminTextWhenBuy(GetAdminStrings.getBuyStringForAdmin(basketCRUD, String.valueOf(myFirstTelegramBot.getChatAdminId()), String.valueOf(chatId)),
                                    String.valueOf(chatId));
                        }
                    }
                    else{
                            doSendText("Невозможно редактировать если заказ активен", String.valueOf(chatId));
                        }
                    break;
                case "Убрать заказ":
                    if(!basketCRUD.isOrder(String.valueOf(chatId)))doSendText("Заказ не активен");
                    else {
                        doSendText(chatId + " отказался от заказа", String.valueOf(myFirstTelegramBot.getChatAdminId()));
                        basketCRUD.setOrder(String.valueOf(chatId), false);
                        basketCRUD.resetBasket(String.valueOf(chatId));
                        doSendText("Успешно");
                    }

                default:
                doSendText("Выберите категорию");
            }
        }
    }

    public void doSendProducts(String s) {
            ArrayList<Object[]> arrayList = productsCRUD.getProductsByCategory(s);
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
                    getTextMessage(text, String.valueOf(myFirstTelegramBot.getChatAdminId())).
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
                    setText(basketCRUD.getBasketInfo(chatId)).
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
