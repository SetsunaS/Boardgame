package setsuna.boardgame.database;

import setsuna.boardgame.utils.Constants;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseManager{
    public static Connection getConnection(){
        Properties properties=new Properties();
        try{
            properties.load(new FileInputStream(Constants.databasePropertiesPath));
            String url=properties.getProperty("database.url");
            String username=properties.getProperty("database.username");
            String password=properties.getProperty("database.password");
            return DriverManager.getConnection(url, username, password);
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Database connection error.");
            return null;
        }
    }
}