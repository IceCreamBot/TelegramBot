package ru.home.fiirst_bot.DataBase;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import java.sql.*;
import java.util.ArrayList;

@Getter
public class ConnectionDB implements Dao {
    private String urlPhotoDefault = "https://prof-lic.com/upload/medialibrary/5f3/5f30deb314f64899cd1c46a3a4f561c3.png";
    private String user = "osbhcodapvhigr";
    private String password = "0c329a33fdcd10e0e4a01dc0ace814caedc7f62b55682b528ac4a239c3f47a77";
    private String url = "jdbc:postgresql://ec2-34-253-148-186.eu-west-1.compute.amazonaws.com:5432/dem1khtuirargt";

    @Override
    public String create(String[] s) {
        String result = "Неправильный запрос";
        PreparedStatement statement = getStatement(SQL.CREATEwithCategory.QUERY);

            try {
                if (!equalsWithCategories(s[4])) return "Неправильная категория";
                statement.setString(1, s[1]);
                statement.setInt(2, Integer.parseInt(s[2]));
                statement.setInt(3, Integer.parseInt(s[3]));

                if(s.length == 5)  statement.setString(4, s[4]);
                else if(s.length == 4) statement.setString(4, "Другое");

                statement.setString(5, urlPhotoDefault);
                statement.execute();
                result = "Успешно";
            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
            }

        return result;
    }

    @Override
    public String createCategory(String[] s){
        String result = "Неправильный запрос";
        PreparedStatement statement = getStatement(SQL.CREATECategory.QUERY);
        if (s[0].equals("добавить категорию")) {
            try {
                if (equalsWithCategories(s[1])) return "Такая категория уже есть";
                statement.setString(1, s[1]);
                statement.execute();
                result = "Успешно";
            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    public ArrayList<Object[]> read(String s){
        ArrayList<Object[]> arrayList = new ArrayList<>();
        PreparedStatement statement = getStatement(SQL.READ.QUERY);
        try {
            statement.setString(1, s);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                arrayList.add(new Object[]{resultSet.getString("name"),
                        resultSet.getInt("quantity"),
                        resultSet.getInt("price"),
                        resultSet.getString("url")});
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    @Override
    public String update(String[] s){
        String result = "Неправильный запрос";
        PreparedStatement statement = null;

        if(s[0].equals("изменить цену"))statement = getStatement(SQL.UPDATEPRICE.QUERY);
        else if(s[0].equals("изменить колличество")) statement = getStatement(SQL.UPDATEQUANTITY.QUERY);
        else if(s[0].equals("изменить фото"))statement = getStatement(SQL.UPDATEURL.QUERY);

        try  {
                if (!equalsWithNames(s[1])) return "Такого названия нету";

                if(s[0].equals("изменить фото")) statement.setInt(1, Integer.parseInt(s[2]));
                else statement.setString(1, s[2]);

                statement.setString(2, s[1]);
                statement.execute();
                result = "Успешно";
            } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            }


        return result;
    }

    @Override
    public String delete(String... s){
        String result = "Неправильный запрос";

        if (s[0].equals("удалить")) {
            PreparedStatement statement = getStatement(SQL.DELETE.QUERY);
            try {
                if (!equalsWithNames(s[1])) return "Такого названия нету";
                statement.setString(1, s[1]);
                statement.execute();
                result = "Успешно";
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public String deleteCategory(String[] s) {
        String result = "Неправильный запрос";
        if(s[0].equals("удалить категорию")) {
            PreparedStatement statement = getStatement(SQL.DELETECategories.QUERY);
            try {
                statement.setString(1, s[1]);
                statement.execute();
                delete("удалить", s[1]);
                result = "Успешно";
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
        return result;
    }

    enum SQL {

        CREATEwithCategory("INSERT INTO PRODUCTS (id, name, quantity, price, category, url) VALUES (DEFAULT, ?, ?, ?, ?, ?);"),
        CREATECategory("INSERT INTO CATEGORIES (name) values (?);"),
        READ("SELECT * FROM PRODUCTS WHERE category = ? ORDER BY name;"),
        READAllCategories("SELECT name FROM categories ORDER BY name;"),
        UPDATEPRICE("Update PRODUCTS set price = ? where name = ?;"),
        UPDATEURL("Update PRODUCTS set url = ? where name = ?;"),
        UPDATEQUANTITY("Update PRODUCTS set quantity = ? where name = ?;"),
        DELETE("delete from products where name = ?;"),
        DELETECategories("Delete FROM PRODUCTS WHERE category = ?;");

        String QUERY;

        SQL(String QUERY) {
            this.QUERY = QUERY;
        }
    }

    public boolean equalsWithCategories(String s) {
        return equalsWith(s, getStatement("SELECT name from categories;"));
    }


    public boolean equalsWithNames(String s){
        return equalsWith(s, getStatement("SELECT name from products;"));
    }

    private boolean equalsWith(String s, PreparedStatement statement){
        boolean result = false;
        try{
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (result = resultSet.getString("name").equals(s)) break;
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  result;
    }

    public ArrayList<String> getAllCategories(){
        ArrayList<String> strings = new ArrayList<>();
        PreparedStatement statement = getStatement(SQL.READAllCategories.QUERY);
        try {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                strings.add(resultSet.getString("name"));
            }
            resultSet.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return strings;
    }

    public PreparedStatement getStatement(String sqlQuery){
        PreparedStatement statement = null;
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement1 = connection.prepareStatement(sqlQuery)){
            statement = statement1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statement;
    }
}
