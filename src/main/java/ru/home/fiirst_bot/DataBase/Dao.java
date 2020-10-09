package ru.home.fiirst_bot.DataBase;

import java.sql.SQLException;
import java.util.ArrayList;

public interface Dao {
    String create(String[] s) throws SQLException;
    ArrayList<Object[]> read(String s) throws SQLException;
    String update(String[] s) throws SQLException;
    String delete(String[] s) throws SQLException;
}
