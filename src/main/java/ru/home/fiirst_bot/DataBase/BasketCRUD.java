package ru.home.fiirst_bot.DataBase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BasketCRUD extends ConnectionToDB{
    ProductsCRUD productsCRUD;

    public BasketCRUD(ProductsCRUD productsCRUD) {
        super();
        this.productsCRUD = productsCRUD;
    }

    public void addProductToBasketBD(String productName){
        String columnName = "column" + productsCRUD.getId(productName);

        String sql = "alter table basket " +
                "add column " + columnName + " varchar(30) not null default '0'; " +
                "update basket set " + columnName + " = '"+ productName + "' where chatid = 'productName'";

        try(PreparedStatement statement = getStatement(sql)){
            statement.execute();
        }
        catch(SQLException e){
            System.out.println("Ошибка при добавлении продукта в корзину");
        }
    }

    public void delProductFromBasketBD(String productName){
        String columnName = "column" + productsCRUD.getId(productName);

        String sql = "alter table basket " +
                "drop column " + columnName;

        try(PreparedStatement statement = getStatement(sql)){
            statement.execute();
        }
        catch(SQLException e){
            System.out.println("Ошибка при удалении из корзины");
        }
    }

    public void plusCountOfProductInBd(String productName, String chatId) {
        String column = "column" + productsCRUD.getId(productName);

        try (PreparedStatement statement1 = getStatement("select " + column + " from basket where chatid = ?");
             PreparedStatement statement2 = getStatement("update basket set " + column + " = ? where chatid = ?")) {
            int quantityInBasket = 0;


            statement1.setString(1, chatId);
            ResultSet rs1 = statement1.executeQuery();

            if (rs1.next()) quantityInBasket = Integer.parseInt(rs1.getString(column));
            rs1.close();

            Integer quantityInProducts = productsCRUD.getQuantity(productName);

                if(quantityInBasket < quantityInProducts) {
                    statement2.setString(1, String.valueOf(quantityInBasket + 1));
                    statement2.setString(2, chatId);
                    statement2.execute();
                }

        } catch (SQLException e) {
            System.out.println("Ошибка при дабовлении продукта в корзину");
        }
    }

    public void minusCountOfProductInBd(String productName, String chatId) {
        String column = "column" + productsCRUD.getId(productName);

        try (PreparedStatement statement1 = getStatement("select " + column + " from basket where chatid = ?");
             PreparedStatement statement2 = getStatement("update basket set " + column + " = ? where chatid = ?")) {
            int quantityInBasket = 0;

            statement1.setString(1, chatId);
            ResultSet rs1 = statement1.executeQuery();

            if (rs1.next()) quantityInBasket = Integer.parseInt(rs1.getString(column));
            rs1.close();


            if(quantityInBasket > 0) {
                statement2.setString(1, String.valueOf(quantityInBasket - 1));
                statement2.setString(2, chatId);
                statement2.execute();
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при удалении продукта в корзину");
        }
    }


    public void updateBasketAfterBuy(String chatId) {
        int totalProducts = productsCRUD.getAllProductsId().size();

        try (PreparedStatement statement1 = getStatement("select * from basket where chatid = ?");
             PreparedStatement statement2 = getStatement("select * from basket where chatid = 'productName';")) {
            statement1.setString(1, chatId);
            ResultSet rs1 = statement1.executeQuery();
            ResultSet rs2 = statement2.executeQuery();

            if (rs1.next() & rs2.next()) {
                    for (int i = 0; i < totalProducts; i++) {
                        Integer countInBasket = Integer.parseInt(rs1.getString(i + 4));
                        String productName = rs2.getString(i + 4);
                        if (countInBasket != 0) {
                            Integer couldBeCountInBasket = productsCRUD.getQuantity(productName) - countInBasket;
                            productsCRUD.updateQuantity(productName, couldBeCountInBasket);
                        }
                    }
                }

            rs1.close();
            rs2.close();
            resetBasket(chatId);
        }
        catch (SQLException e){
            System.out.println("Ошибка при обновлении корзины");
        }
    }

    public void resetBasket(String chatId){
        try(PreparedStatement statement = getStatement("delete from basket where chatid = ?")){
            statement.setString(1, chatId);
            statement.execute();
        }
        catch (SQLException e){
            System.out.println("Ошибка при удалении корзины");
        }
    }

    public String getBasketInfo(String chatId){
        int totalPrice = 0;
        int totalProducts = productsCRUD.getAllProductsId().size();

        StringBuilder stringBuilder = new StringBuilder();

        try(PreparedStatement statement1 = getStatement("select * from basket where chatid = ?");
            PreparedStatement statement2 = getStatement("select * from basket where chatid = 'productName';")){
            statement1.setString(1, chatId);
            ResultSet rs1 = statement1.executeQuery();
            ResultSet rs2 = statement2.executeQuery();

            if (rs1.next() & rs2.next()){
                    for (int i = 0; i < totalProducts; i++) {
                        int countInBasket = Integer.parseInt(rs1.getString(i + 4));
                        String productName = rs2.getString(i + 4);
                        if(countInBasket != 0) {
                            stringBuilder.append(productName + " " + countInBasket + " | ");
                            totalPrice += countInBasket * productsCRUD.getPrice(productName);
                        }
                    }
                }

            rs1.close();
            rs2.close();

            stringBuilder.append("ВСЕГО " + totalPrice + " руб.");
        }
        catch (SQLException e){
            System.out.println("Ошибка при взятие информации из корзины");
        }

        return stringBuilder.toString();
    }

    public void initBasket(String chatId){
        try(PreparedStatement statement = getStatement("Select chatid from basket")){
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                if (rs.getString("chatid").equals(chatId)) return;
            }
            try(PreparedStatement statement1 = getStatement("insert into basket (chatid) values (?)")) {
                statement1.setString(1, chatId);
                statement1.execute();
            }
        }
        catch (SQLException e){
            System.out.println("Ошибка при инициализации корзины");
        }
    }

    public void setOrder(String chatId, boolean isOrder){
        String sql;
        if(isOrder) sql = "update basket set isorder = true where chatid = ?";
        else sql = "update basket set isorder = false where chatid = ?";

        try(PreparedStatement statement = getStatement(sql)){
            statement.setString(1, chatId);
            statement.execute();
        }
        catch (SQLException e){
            System.out.println("Ошибка при установки состояния заказа");
        }
    }

    public boolean isOrder(String chatId){
        boolean result = false;
        try(PreparedStatement statement = getStatement("select isorder from basket where chatid = ?")) {
            statement.setString(1, chatId);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                result = rs.getBoolean("isorder");
            }
        }
        catch (SQLException e) {
            System.out.println("Ошибка при проверке состояния заказа");
        }
        return result;
    }
}
