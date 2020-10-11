package ru.home.fiirst_bot.Keyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.home.fiirst_bot.DataBase.ConnectionDB;
import java.util.ArrayList;
import java.util.List;

public class Keyboards {

    public static ReplyKeyboard getMenuKeyboard(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        ArrayList<String> list = new ConnectionDB().getAllCategories();

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

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboard getContextKeyboard(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton("В меню"));

        keyboardRows.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

}
