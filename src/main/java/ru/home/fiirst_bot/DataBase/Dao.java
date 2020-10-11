package ru.home.fiirst_bot.DataBase;

import java.util.ArrayList;

public interface Dao {
    String createCategory(String[] s);
    String create(String[] s);
    ArrayList<Object[]> read(String s);
    String update(String[] s);
    String delete(String[] s);
    String deleteCategory(String[] s);
}
