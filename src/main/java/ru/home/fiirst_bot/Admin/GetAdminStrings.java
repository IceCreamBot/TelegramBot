package ru.home.fiirst_bot.Admin;

import ru.home.fiirst_bot.DataBase.BasketCRUD;

public class GetAdminStrings {

    public static String getInfoString(){
        return "Команды:\n" +
                "добавить.(название).(колличество).(категория)\n" +
                "добавить категорию.(название категории)\n" +
                "изменить цену.(название товара).(новая цена)\n" +
                "изменить колличество.(название товара).(новое  колличество)\n" +
                "изменить фото.(название товара).(ссылка на фото)\n" +
                "удалить.(название товара)\n" +
                "удалить категорию.(название категории)\n" +
                "/admin - справка по командам";
    }
    public static String getBuyString(String basketInfoString, String chatId){
        return  "Ваш заказ:\n\n" +
                basketInfoString + "\n\n" +
                "Оплата на карту сбербанка по номеру телефона +7 (977)-545-77-58\n\n" +
                "После оплаты проходите в 314/3 направо";
    }

    public static String getBuyStringForAdmin(BasketCRUD basketCRUD, String AdminId, String chatId){
    return "У вас хотят заказать id " + chatId + "\n\n" +
            basketCRUD.getBasketInfo(AdminId) + "\n\n" +
            "Пришла оплата?";
    }
}
