package ru.home.fiirst_bot.Admin;

public class GetAdminStrings {

    public static String getInfoString(){
        return "Команды:\n" +
                "добавить.(название).(колличество).(категория)\n" +
                "добавить категорию.(название категории)\n" +
                "изменить цену.(название товара).(новая цена)\n" +
                "изменить колличество.(название товара).(новое  колличество)\n" +
                "изменить фото.(название товара).(ссылка на фото)\n" +
                "удалить.(название товара)\n" +
                "/admin - справка по командам";
    }
}