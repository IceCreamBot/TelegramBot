package ru.home.fiirst_bot.DataBase;

import lombok.Getter;
import java.sql.*;
import java.util.ArrayList;

@Getter
public class ConnectionDB implements Dao {
    private String urlPhotoDefault;
    private Connection connection;
    private String user = "osbhcodapvhigr";
    private String password = "0c329a33fdcd10e0e4a01dc0ace814caedc7f62b55682b528ac4a239c3f47a77";
    private String url = "jdbc:postgresql://ec2-34-253-148-186.eu-west-1.compute.amazonaws.com:5432/dem1khtuirargt";

    public ConnectionDB() {
        try {
            this.connection =  DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ConnectionDB(String urlPhotoDefault) {
        this.urlPhotoDefault = urlPhotoDefault;
        try {
            this.connection =  DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String create(String[] s) {
        String result = "Неправильный запрос";
        try (PreparedStatement statement = getStatement(SQL.CREATEwithCategory.QUERY)){
                if (s.length < 5) return  "Не указана категория";
                if (!equalsWithCategories(s[4])) return "Неправильная категория";
                statement.setString(1, s[1]);
                statement.setInt(2, Integer.parseInt(s[2]));
                statement.setInt(3, Integer.parseInt(s[3]));
                statement.setString(4, s[4]);

                statement.setString(5, urlPhotoDefault);
                statement.execute();

                addProductToBasketBD(s[1]);

                result = "Успешно";
            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
            }

        return result;
    }

    @Override
    public String createCategory(String[] s){
        String result = "Неправильный запрос";
        if (s[0].equals("добавить категорию")) {
            try (PreparedStatement statement = getStatement(SQL.CREATECategory.QUERY)){
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
    public ArrayList<Object[]> read(String categoryName){
        ArrayList<Object[]> arrayList = new ArrayList<>();
        try (PreparedStatement statement = getStatement(SQL.READ.QUERY)){
            statement.setString(1, categoryName);
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

    public Integer readPrice(String productName){
        Integer result = 0;
        try(PreparedStatement statement = getStatement(SQL.READPrice.QUERY)){
            statement.setString(1, productName);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                result = rs.getInt("price");
            }
            rs.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    public String readId(String productName) {
        String result = null;
        try(PreparedStatement statement = getStatement(SQL.READProductId.QUERY)){
            statement.setString(1, productName);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                result = rs.getString("id");
            }
            rs.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    public Integer readQuantity(String productName){
        Integer count = 0;
        try(PreparedStatement statement = getStatement("select quantity from products where name = '" + productName + "';")){
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                count = rs.getInt("quantity");
            }
            rs.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return count;
    }

    public ArrayList<String> getAllProductsId(){
        ArrayList<String> result = new ArrayList<>();
        try(PreparedStatement statement = getStatement("select id from products;")){
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                result.add(rs.getString("id"));
            }
            rs.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String update(String[] s){
        String result = "Неправильный запрос";
        PreparedStatement statement1 = null;

        if(s[0].equals("изменить цену"))statement1 = getStatement(SQL.UPDATEPRICE.QUERY);
        else if(s[0].equals("изменить колличество")) statement1 = getStatement(SQL.UPDATEQUANTITY.QUERY);
        else if(s[0].equals("изменить фото"))statement1 = getStatement(SQL.UPDATEURL.QUERY);

        try(PreparedStatement statement = statement1) {
                if (!equalsWithNames(s[1])) return "Такого названия нету";

                if(!s[0].equals("изменить фото")) statement.setInt(1, Integer.parseInt(s[2]));
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
            try(PreparedStatement statement = getStatement(SQL.DELETE.QUERY)){
                if (!equalsWithNames(s[1])) return "Такого названия нету";
                statement.setString(1, s[1]);
                delProductFromBasketBD(s[1]);
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
            try( PreparedStatement statement = getStatement(SQL.DELETECategories.QUERY)) {
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
        READPrice("Select price from products where name = ?;"),
        READProductId("Select id from products where name = ?;"),
        READAllCategories("SELECT name FROM categories ORDER BY name;"),
        UPDATEPRICE("Update PRODUCTS set price = ? where name = ?;"),
        UPDATEURL("Update PRODUCTS set url = ? where name = ?;"),
        UPDATEQUANTITY("Update PRODUCTS set quantity = ? where name = ?;"),
        DELETE("delete from products where name = ?;"),
        DELETECategories("Delete FROM categories WHERE name = ?;");

        String QUERY;

        SQL(String QUERY) {
            this.QUERY = QUERY;
        }
    }

    public boolean equalsWithCategories(String s) {
        PreparedStatement statement = getStatement("SELECT name from categories;");
            return equalsWith(s, statement);
    }


    public boolean equalsWithNames(String s){
            PreparedStatement statement = getStatement("SELECT name from products;");
            return equalsWith(s, statement);
    }

    private boolean equalsWith(String s, PreparedStatement statement){
        boolean result = false;
        try(PreparedStatement statement1 = statement){
                ResultSet resultSet = statement1.executeQuery();
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
        try (PreparedStatement statement = getStatement(SQL.READAllCategories.QUERY)){
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
        try {
            PreparedStatement statement1 = connection.prepareStatement(sqlQuery);
            statement = statement1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statement;
    }

    public void addProductToBasketBD(String productName){
        String columnname = "column" + readId(productName);

        StringBuilder sql = new StringBuilder("alter table basket ").append(
                "add column " + columnname + " varchar(30) not null default '0'; ").append(
                "update basket set " + columnname + " = '"+ productName + "' where chatid = 'productName'; ");

        try(PreparedStatement statement = getStatement(sql.toString())){
            statement.execute();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void delProductFromBasketBD(String productName){
        String columnname = "column" + readId(productName);

        StringBuilder sql = new StringBuilder("alter table basket ").append(
                "drop column " + columnname + ";");

        try(PreparedStatement statement = getStatement(sql.toString())){
            statement.execute();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void updateCountOfProductInBd(String[] productName, String chatId) {
        String column = "column" + readId(productName[0]);

            try (PreparedStatement statement1 = getStatement("select " + column + " from basket where chatid = '" + chatId + "';");
                 PreparedStatement statement2 = getStatement("update basket set " + column + " = ? where chatid = '" + chatId + "';")) {
                Integer currentCount = 0;
                ResultSet rs1 = statement1.executeQuery();
                if (rs1.next()) currentCount = Integer.parseInt(rs1.getString(column));
                rs1.close();

                Integer newCount;
                Integer quantity = readQuantity(productName[0]);

                if(productName[1].equals("+")) {
                    newCount = currentCount + 1;
                    if(newCount <= quantity) {
                        statement2.setString(1, String.valueOf(newCount));
                        statement2.execute();
                    }
                }
                else if (productName[1].equals("-")) {
                    newCount = currentCount - 1;
                    if(newCount >= 0)  {
                        statement2.setString(1, String.valueOf(newCount));
                        statement2.execute();
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public void updateBasketAfterBuy(String chatId) {
        ArrayList<String> productId = getAllProductsId();

        try (PreparedStatement statement1 = getStatement("select * from basket where chatid = '" + chatId + "';");
             PreparedStatement statement2 = getStatement("select * from basket where chatid = 'productName';")) {
            ResultSet rs1 = statement1.executeQuery();
            ResultSet rs2 = statement2.executeQuery();

            if (rs1.next()) {
                if (rs2.next()) {
                    for (int i = 0; i < productId.size(); i++) {
                        Integer countInBasket = Integer.parseInt(rs1.getString(i + 4));
                        String productName = rs2.getString(i + 4);
                        if (countInBasket != 0) {
                            String couldBeCount = String.valueOf(readQuantity(productName) - countInBasket);
                            String[] string = new String[]{"изменить колличество", productName, couldBeCount};
                            update(string);
                        }
                    }
                }
            }
            rs1.close();
            rs2.close();
            resetBasket(chatId);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void resetBasket(String chatId){
        try(PreparedStatement statement = getStatement("delete from basket where chatid = '" + chatId + "';")){
            statement.execute();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public String getBasketInfo(String chatId){
        Integer totalprice = 0;
        ArrayList<String> productId = getAllProductsId();

        StringBuilder stringBuilder = new StringBuilder();

        try(PreparedStatement statement1 = getStatement("select * from basket where chatid = '" + chatId + "';");
        PreparedStatement statement2 = getStatement("select * from basket where chatid = 'productName';")){
        ResultSet rs1 = statement1.executeQuery();
        ResultSet rs2 = statement2.executeQuery();

            if (rs1.next()) {
                if (rs2.next()){
                    for (int i = 0; i < productId.size(); i++) {
                        if(!rs1.getString(i + 4).equals("0")) {
                            stringBuilder.append(rs2.getString(i + 4) + " " + rs1.getString(i + 4) + " | ");
                            totalprice += Integer.parseInt(rs1.getString(i + 4)) * readPrice(rs2.getString(i + 4));
                        }
                    }
                }
            }
            rs1.close();
            rs2.close();

            stringBuilder.append("ВСЕГО " + totalprice + " руб.");
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public void initBasket(String chatId){
        try(PreparedStatement statement = getStatement("Select chatid from basket")){
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                if (rs.getString("chatid").equals(chatId)) return;
            }
                    try(PreparedStatement statement1 = getStatement("insert into basket (id, chatid) values (default, ?);")) {
                    statement1.setString(1, chatId);
                    statement1.execute();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setOrder(String chatId, boolean isOrder){
        String sql = null;
        if(isOrder) sql = "update basket set isorder = true where chatid = '" + chatId + "';";
        else sql = "update basket set isorder = false where chatid = '" + chatId + "';";

        try(PreparedStatement statement = getStatement( sql)){
            statement.execute();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean isOrder(String chatId){
        boolean result = false;
        try(PreparedStatement statement = getStatement("select isorder from basket where chatid = '" + chatId + "';")) {
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
               result = rs.getBoolean("isorder");
            }
        }
        catch (SQLException e) {
        e.printStackTrace();
        }
        return result;
    }
}
