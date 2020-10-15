package ru.home.fiirst_bot.DataBase;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Getter
public abstract class ConnectionToDB {
    private final String user = "osbhcodapvhigr";
    private final String password = "0c329a33fdcd10e0e4a01dc0ace814caedc7f62b55682b528ac4a239c3f47a77";
    private final String url = "jdbc:postgresql://ec2-34-253-148-186.eu-west-1.compute.amazonaws.com:5432/dem1khtuirargt";
    private Connection connection;

    public ConnectionToDB() {
        try {
            this.connection =  DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
