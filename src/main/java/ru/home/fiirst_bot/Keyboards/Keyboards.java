package ru.home.fiirst_bot.Keyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.home.fiirst_bot.DataBase.ProductsCRUD;

import java.util.ArrayList;
import java.util.List;

public class Keyboards {
    public static ReplyKeyboard getMenuKeyboard(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        ArrayList<String> list = new ProductsCRUD().getAllCategories();

        int rowsCount = (int) (Math.ceil((double) list.size()/3.00));

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        int counter = 0;
        for (int i = 0; i < rowsCount; i++) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for (int j = 0; j < 3; j++) {
                if(counter == list.size())break;
                keyboardRow.add(new KeyboardButton(list.get(counter)));
                counter++;
            }
            keyboardRows.add(keyboardRow);
        }

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("Убрать заказ"));
        keyboardRow.add(new KeyboardButton("Сбросить корзину"));
        keyboardRow.add(new KeyboardButton("Купить"));
        keyboardRows.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public static InlineKeyboardMarkup getInlineKeyboardMarkupForProducts(String productName){
        List<List<InlineKeyboardButton>> inlineKeyboard = new ArrayList<>();
        List<InlineKeyboardButton> rowButtons = new ArrayList<>();

        rowButtons.add(new InlineKeyboardButton().setText("-1").setCallbackData(productName + ".-"));
        rowButtons.add(new InlineKeyboardButton().setText("+1").setCallbackData(productName + ".+"));

        inlineKeyboard.add(rowButtons);

        return new InlineKeyboardMarkup().setKeyboard(inlineKeyboard);
    }

    public static InlineKeyboardMarkup getInlineKeyboardMarkupForAdminWhenBuy(String chatId){
        List<List<InlineKeyboardButton>> inlineKeyboard = new ArrayList<>();
        List<InlineKeyboardButton> rowButtons = new ArrayList<>();

        rowButtons.add(new InlineKeyboardButton().setText("Нет").setCallbackData("-"));
        rowButtons.add(new InlineKeyboardButton().setText("Да").setCallbackData(chatId));

        inlineKeyboard.add(rowButtons);

        return new InlineKeyboardMarkup().setKeyboard(inlineKeyboard);
    }
}
