package ru.home.fiirst_bot.DataBase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.*;
import java.util.ArrayList;

public class ConnectionDB implements Dao{
  private String user = "postgres";
  private String password = "1234";
  private String url = "postgres://osbhcodapvhigr:0c329a33fdcd10e0e4a01dc0ace814caedc7f62b55682b528ac4a239c3f47a77@ec2-34-253-148-186.eu-west-1.compute.amazonaws.com:5432/dem1khtuirargt";



    @Override
    public String create(String[] s) throws SQLException {
        String result = "Неправильный запрос";
        if(s.length == 5) {
            try (Connection connection = DriverManager.getConnection(url);
                 PreparedStatement statement = connection.prepareStatement(SQL.CREATEwithCategory.QUERY)) {
                if(!equalsWithCategories(s[4], connection))return "Неправильная категория";
                statement.setString(1, s[1]);
                statement.setInt(2, Integer.parseInt(s[2]));
                statement.setInt(3, Integer.parseInt(s[3]));
                statement.setString(4, s[4]);
                statement.setString(5, "https://prof-lic.com/upload/medialibrary/5f3/5f30deb314f64899cd1c46a3a4f561c3.png");

                statement.execute();
                result = "Успешно";
            }
            catch (NumberFormatException e){
            }
        }
        else if(s.length == 4){
            try (Connection connection = DriverManager.getConnection(url);
                 PreparedStatement statement = connection.prepareStatement(SQL.CREATEwithCategory.QUERY)) {
                statement.setString(1, s[1]);
                statement.setInt(2, Integer.parseInt(s[2]));
                statement.setInt(3, Integer.parseInt(s[3]));
                statement.setString(4, "Другое");
                statement.execute();
                result = "Успешно";
            }
            catch (NumberFormatException e){
            }
        }
        else if(s[0].equals("добавить категорию")){
            try (Connection connection = DriverManager.getConnection(url);
                 PreparedStatement statement = connection.prepareStatement(SQL.CREATECategory.QUERY)) {
                if(equalsWithCategories(s[1], connection))return "Такая категория уже есть";
                statement.setString(1, s[1]);
                statement.execute();
                result = "Успешно";
            }
            catch (NumberFormatException e){
            }
        }

        return result;
    }

    @Override
    public ArrayList<Object[]> read(String s) throws SQLException{
            ArrayList<Object[]> arrayList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(SQL.READ.QUERY)) {
            statement.setString(1, s);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                arrayList.add(new Object[]{resultSet.getString("name"),
                resultSet.getInt("quantity"),
                resultSet.getInt("price"),
                resultSet.getString("url")});
            }
        }
        return arrayList;
    }

    @Override
    public String update(String[] s) throws SQLException{
        String result = "Неправильный запрос";
        if(s[0].equals("изменить цену")) {
            try (Connection connection = DriverManager.getConnection(url);
                 PreparedStatement statement = connection.prepareStatement(SQL.UPDATEPRICE.QUERY)) {
                if(!equalsWithNames(s[1], connection))return "Такого названия нету";
                statement.setInt(1, Integer.parseInt(s[2]));
                statement.setString(2, s[1]);
                statement.execute();
                result = "Успешно";
            } catch (NumberFormatException e) {
            }
        }
        else if(s[0].equals("изменить колличество")){
            try (Connection connection = DriverManager.getConnection(url);
                 PreparedStatement statement = connection.prepareStatement(SQL.UPDATEQUANTITY.QUERY)) {
                if(!equalsWithNames(s[1], connection))return "Такого названия нету";
                statement.setInt(1, Integer.parseInt(s[2]));
                statement.setString(2, s[1]);
                statement.execute();
                result = "Успешно";
            } catch (NumberFormatException e) {
            }
        }
        else if(s[0].equals("изменить фото")){
            try (Connection connection = DriverManager.getConnection(url);
                 PreparedStatement statement = connection.prepareStatement(SQL.UPDATEURL.QUERY)) {
                if(!equalsWithNames(s[1], connection))return "Такого названия нету";
                statement.setString(1, s[2]);
                statement.setString(2, s[1]);
                statement.execute();
                result = "Успешно";
            }
        }

        return result;
    }

    @Override
    public String delete(String[] s) throws SQLException{
            String result = "Неправильный запрос";
        if(s[0].equals("удалить")) {
            try (Connection connection = DriverManager.getConnection(url);
                 PreparedStatement statement = connection.prepareStatement(SQL.DELETE.QUERY)) {
                if (!equalsWithNames(s[1], connection)) return "Такого названия нету";
                statement.setString(1, s[1]);
                statement.execute();
                result = "Успешно";
                return result;
            }
        }
        else if(s[0].equals("удалить категорию")) {
            try (Connection connection = DriverManager.getConnection(url);
                 PreparedStatement statement = connection.prepareStatement(SQL.DELETECategories.QUERY)) {
                if (!equalsWithCategories(s[1], connection)) return "Такого названия нету";
                statement.setString(1, s[1]);
                statement.execute();
                result = "Успешно";
            }
        }
        return result;
    }

    enum SQL{

        CREATEwithCategory("INSERT INTO PRODUCTS (id, name, quantity, price, category, url) VALUES (DEFAULT, ?, ?, ?, ?, ?);"),
        CREATECategory("INSERT INTO CATEGORIES (name) values (?);"),
        READ("SELECT * FROM PRODUCTS WHERE category = ? ORDER BY name"),
        UPDATEPRICE("Update PRODUCTS set price = ? where name = ?;"),
        UPDATEURL("Update PRODUCTS set url = ? where name = ?;"),
        UPDATEQUANTITY("Update PRODUCTS set quantity = ? where name = ?;"),
        DELETE("delete from products where name = ?;"),
        DELETECategories("delete from categories where name = ?;");

        String QUERY;

        SQL(String QUERY){this.QUERY = QUERY;}
    }

    public boolean equalsWithCategories(String s, Connection connection) throws SQLException{
        boolean result = false;
        PreparedStatement statement = connection.prepareStatement("SELECT * from categories");
        ResultSet rs = statement.executeQuery();
        statement.close();
        while (rs.next()){
            if(result = rs.getString("name").equals(s))break;
            }
        return result;
    }
    public boolean equalsWithNames(String s, Connection connection) throws SQLException{
        boolean result = false;
        PreparedStatement statement = connection.prepareStatement("SELECT name from products");
        ResultSet rs = statement.executeQuery();
        statement.close();
        while (rs.next()){
            if(result = rs.getString("name").equals(s))break;
        }
        return result;
    }
}
