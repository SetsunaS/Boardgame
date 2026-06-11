package setsuna.boardgame.utils.database;

import setsuna.boardgame.GameApplication;
import setsuna.boardgame.utils.Constants;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager{
    private static Connection connection;
    private static String url;
    private static String username;
    private static String password;

    //Permet aux tests de cibler une autre base de données que celle de database.properties
    static synchronized void overrideConnectionConfiguration(String newUrl, String newUsername, String newPassword){
        try{
            if(connection!=null && !connection.isClosed()) connection.close();
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Error while closing connection.");
        }

        connection=null;
        url=newUrl;
        username=newUsername;
        password=newPassword;
    }

    private static void loadConfiguration() throws Exception{
        Properties properties=new Properties();
        properties.load(GameApplication.class.getResourceAsStream(Constants.DATABASE_PROPERTIES_PATH));
        url=properties.getProperty("database.url");
        username=properties.getProperty("database.username");
        password=properties.getProperty("database.password");
    }

    private static synchronized Connection getConnection(){
        try{
            if(connection==null || connection.isClosed()){
                if(url==null) loadConfiguration();
                connection=DriverManager.getConnection(url, username, password);
            }
            return connection;
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Database connection error.");
            return null;
        }
    }

    public static boolean usernameAlreadyTaken(String username){
        String query="SELECT * FROM player WHERE username=?";
        Connection connection=DatabaseManager.getConnection();
        if(connection==null) return false;

        try(PreparedStatement statement=connection.prepareStatement(query)){
            statement.setString(1, username);

            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()) return true;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Error while selecting player.");
        }

        return false;
    }

    public static boolean emailAlreadyTaken(String email){
        String query="SELECT * FROM player WHERE email=?";
        Connection connection=DatabaseManager.getConnection();
        if(connection==null) return false;

        try(PreparedStatement statement=connection.prepareStatement(query)){
            statement.setString(1, email);

            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()) return true;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Error while selecting player.");
        }

        return false;
    }

    public static boolean insertNewPlayer(String username, String email, String hashedPassword){
        String query="INSERT INTO player(username, email, hashed_password) VALUES (?, ?, ?)";
        Connection connection=DatabaseManager.getConnection();
        if(connection==null) return false;

        try(PreparedStatement statement=connection.prepareStatement(query)){
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, hashedPassword);

            if(statement.executeUpdate()>0) return true;
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Error while inserting new player.");
        }

        return false;
    }

    public static boolean insertNewScore(String username, int score){
        String query="INSERT INTO score(score, player_id) VALUES (?, ?)";
        int playerId=getPlayerId(username);

        if(playerId>-1){
            Connection connection=DatabaseManager.getConnection();
            if(connection==null) return false;
            try(PreparedStatement statement=connection.prepareStatement(query)){
                statement.setInt(1, score);
                statement.setInt(2, playerId);

                if(statement.executeUpdate()>0) return true;
            }
            catch(SQLException e){
                e.printStackTrace();
                System.out.println("Error while inserting new score.");
            }
        }

        return false;
    }

    public static boolean insertNewScore(String username){
        return insertNewScore(username, 0);
    }

    public static boolean isUsernameInDatabase(String username){
        String query="SELECT * FROM player WHERE username=? or email=?";
        Connection connection=DatabaseManager.getConnection();
        if(connection==null) return false;

        try(PreparedStatement statement=connection.prepareStatement(query)){
            statement.setString(1, username);
            statement.setString(2, username);

            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()) return true;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Error while selecting player.");
        }

        return false;
    }

    public static int getPlayerId(String username){
        String query="SELECT id FROM player WHERE username=?";
        Connection connection=DatabaseManager.getConnection();
        if(connection==null) return -1;

        try(PreparedStatement statement=connection.prepareStatement(query)){
            statement.setString(1, username);

            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()) return resultSet.getInt(1);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Error while selecting player.");
        }

        return -1;
    }

    public static String getPlayerUsername(String email){
        String query="SELECT username FROM player WHERE email=?";
        Connection connection=DatabaseManager.getConnection();
        if(connection==null) return null;

        try(PreparedStatement statement=connection.prepareStatement(query)){
            statement.setString(1, email);

            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()) return resultSet.getString(1);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Error while selecting player.");
        }

        return null;
    }

    public static String getPlayerHashedPasword(String username){
        String query="SELECT hashed_password FROM player WHERE username=? OR email=?";
        Connection connection=DatabaseManager.getConnection();
        if(connection==null) return null;

        try(PreparedStatement statement=connection.prepareStatement(query)){
            statement.setString(1, username);
            statement.setString(2, username);

            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()) return resultSet.getString(1);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Error while selecting player.");
        }

        return null;
    }

    public static int getScore(String username){
        String query="SELECT score FROM player " +
                     "JOIN score ON player.id=score.player_id " +
                     "WHERE username=? OR email=?";
        Connection connection=DatabaseManager.getConnection();
        if(connection==null) return -1;

        try(PreparedStatement statement=connection.prepareStatement(query)){
            statement.setString(1, username);
            statement.setString(2, username);

            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()) return resultSet.getInt(1);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Error while selecting player.");
        }

        return -1;
    }

    public static boolean updatePlayerScore(String username, int score){
        String query="UPDATE score SET score=? WHERE player_id=?";
        Connection connection=DatabaseManager.getConnection();
        if(connection==null) return false;

        try(PreparedStatement statement=connection.prepareStatement(query)){
            int playerId=getPlayerId(username);
            statement.setInt(1, score);
            statement.setInt(2, playerId);

            if(statement.executeUpdate()>0) return true;
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Error while selecting player.");
        }

        return false;
    }
}