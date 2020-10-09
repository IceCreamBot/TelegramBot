package ru.home.fiirst_bot.DataBase;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.SQLException;
import java.util.ArrayList;

public interface Dao {
    String create(String[] s) throws SQLException;
    ArrayList<Object[]> read(String s) throws SQLException;
    String update(String[] s) throws SQLException;
    String delete(String[] s) throws SQLException;
}
