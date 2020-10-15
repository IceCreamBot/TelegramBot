package ru.home.fiirst_bot.DataBase;

import lombok.Getter;
import java.sql.*;
import java.util.ArrayList;

@Getter
public class ProductsCRUD extends ConnectionToDB{

    private String urlPhotoDefault;

    public ProductsCRUD() {
        super();
    }

    public ProductsCRUD(String urlPhotoDefault) {
        super();
        this.urlPhotoDefault = urlPhotoDefault;
    }

    public String createProduct(String name, String quantity, String price, String category) {
        String result = "Ошибка при добавлении продукта";
        try (PreparedStatement statement = getStatement("INSERT INTO PRODUCTS " +
                "(name, quantity, price, category, url) VALUES " +
                "(?, ?, ?, ?, ?)")){

                statement.setString(1, name);
                statement.setInt(2, Integer.parseInt(quantity));
                statement.setInt(3, Integer.parseInt(price));
                statement.setString(4, category);
                statement.setString(5, urlPhotoDefault);

                if(statement.executeUpdate() != 0) result = "Успешно";

                BasketCRUD basketCRUD = new BasketCRUD(this);
                basketCRUD.addProductToBasketBD(name);
                basketCRUD.closeConnection();

            } catch (NumberFormatException | SQLException e) {
            System.out.println(result);
            }

        return result;
    }

    public String createCategory(String name){
        String result = "Ошибка при создании категории";

        if (equalsWithCategories(name)) return "Такая категория уже есть";

            try (PreparedStatement statement = getStatement("INSERT INTO CATEGORIES (name) values (?)")){

                statement.setString(1, name);

                if(statement.executeUpdate() != 0) result = "Успешно";

            } catch (NumberFormatException | SQLException e) {
                System.out.println(result);
            }

        return result;
    }

    public ArrayList<Object[]> getProductsByCategory(String categoryName){
        if(!equalsWithCategories(categoryName)) return null;

        ArrayList<Object[]> arrayList = new ArrayList<>();

        try (PreparedStatement statement = getStatement("SELECT * FROM PRODUCTS WHERE category = ? ORDER BY name")){

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
            System.out.println("Ошибка при чтении по категории");
        }
        return arrayList;
    }

    public Integer getPrice(String productName){
        Integer result = null;

        try(PreparedStatement statement = getStatement("Select price from products where name = ?")){
            statement.setString(1, productName);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                result = resultSet.getInt("price");
            }

            resultSet.close();
        }
        catch (SQLException e){
            System.out.println("Ошибка при чтении цены");
        }
        return result;
    }

    public Integer getId(String productName) {

        Integer result = null;

        try(PreparedStatement statement = getStatement("Select id from products where name = ?")){

            statement.setString(1, productName);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                result = resultSet.getInt("id");
            }

            resultSet.close();
        }
        catch (SQLException e){
            System.out.println("Ошибка при чтении Id");
        }
        return result;
    }

    public Integer getQuantity(String productName){
        Integer result = null;

        try(PreparedStatement statement = getStatement("select quantity from products where name = ?")){

            statement.setString(1, productName);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                result = resultSet.getInt("quantity");
            }

            resultSet.close();
        }
        catch (SQLException e){
            System.out.println("Ошибка при чтении колличества");
        }
        return result;
    }

    public String updatePrice(String productName, Integer price){
        String result = "Ошибка при обновлении цены";

        try(PreparedStatement statement = getStatement("Update PRODUCTS set price = ? where name = ?")) {

                statement.setInt(1, price);
                statement.setString(2, productName);

                if (statement.executeUpdate() != 0) result = "Успешно";;

            } catch (NumberFormatException | SQLException e) {
            System.out.println(result);
            }

        return result;
    }

    public String updateQuantity(String productName, Integer quantity){
        String result = "Ошибка при обновлении колличества";

        try(PreparedStatement statement = getStatement("Update PRODUCTS set quantity = ? where name = ?")) {

            statement.setInt(1, quantity);
            statement.setString(2, productName);

            if (statement.executeUpdate() != 0) result = "Успешно";;

        } catch (NumberFormatException | SQLException e) {
            System.out.println(result);
        }

        return result;
    }

    public String updateUrl(String productName, String url){
        String result = "Ошибка при обновлении фото";

        try(PreparedStatement statement = getStatement("Update PRODUCTS set url = ? where name = ?")) {

            statement.setString(1, url);
            statement.setString(2, productName);

            if (statement.executeUpdate() != 0) result = "Успешно";;

        } catch (NumberFormatException | SQLException e) {
            System.out.println(result);
        }

        return result;
    }

    public String deleteProduct(String name){

        String result = "Ошибка при удалении продукта";

            try(PreparedStatement statement = getStatement("delete from products where name = ?")){

                statement.setString(1, name);

                BasketCRUD basketCRUD = new BasketCRUD(this);
                basketCRUD.delProductFromBasketBD(name);
                basketCRUD.closeConnection();

                if(statement.executeUpdate() != 0)result = "Успешно";

            } catch (SQLException e) {
                System.out.println(result);
            }

        return result;
    }

    public String deleteCategory(String name) {
        String result = "ошибка при удалии категории";

            try(PreparedStatement statement = getStatement("Delete FROM categories WHERE name = ?; " +
                    "Delete from products where category = ?;")) {

                statement.setString(1, name);
                statement.setString(2, name);

                if(statement.executeUpdate() != 0) result = "Успешно";

            }
            catch (SQLException e){
                System.out.println(result);
            }

        return result;
    }

    public ArrayList<String> getAllCategories(){
        ArrayList<String> strings = new ArrayList<>();

        try (PreparedStatement statement = getStatement("SELECT name FROM categories ORDER BY name")){

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                strings.add(resultSet.getString("name"));
            }

            resultSet.close();
        }
        catch (SQLException e){
            System.out.println("Ошибка при чтении всех категорий");
        }

        return strings;
    }

    public ArrayList<Integer> getAllProductsId(){

        ArrayList<Integer> result = new ArrayList<>();

        try(PreparedStatement statement = getStatement("select id from products")){

            ResultSet rs = statement.executeQuery();

            while (rs.next()){
                result.add(rs.getInt("id"));
            }

            rs.close();
        }
        catch (SQLException e){
            System.out.println("Ошибка при чтении всех id");
        }

        return result;
    }

    public boolean equalsWithCategories(String name) {
        boolean result = false;

        try(PreparedStatement statement = getStatement("SELECT name from categories")){
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                if (result = resultSet.getString("name").equals(name)) break;
            }

            resultSet.close();

        } catch (SQLException e) {
            System.out.println("Ошибка при сравнении категорий");
        }

        return  result;
    }

    private boolean equalsWithName(String name){
        boolean result = false;

        try(PreparedStatement statement = getStatement("SELECT name from products")){
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    if (result = resultSet.getString("name").equals(name)) break;
                }

                resultSet.close();

        } catch (SQLException e) {
            System.out.println("Ошибка при сравнении имен");
        }

        return  result;
    }




}
