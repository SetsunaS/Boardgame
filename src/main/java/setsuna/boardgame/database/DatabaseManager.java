package setsuna.boardgame.database;

import setsuna.boardgame.GameApplication;
import setsuna.boardgame.utils.Constants;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager{
    private static Connection getConnection(){
        Properties properties=new Properties();
        try{
            properties.load(GameApplication.class.getResourceAsStream(Constants.databasePropertiesPath));
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

    public static boolean usernameAlreadyTaken(String username){
        String query="SELECT * FROM player WHERE username=?";
        try(Connection connection=DatabaseManager.getConnection();
            PreparedStatement statement=connection.prepareStatement(query)){
            statement.setString(1, username);

            //Exécute la requête et vérifie s'il y a au moins un résultat
            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()){
                    return true;
                }
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
        try(Connection connection=DatabaseManager.getConnection();
            PreparedStatement statement=connection.prepareStatement(query)){
            statement.setString(1, email);

            //Exécute la requête et vérifie s'il y a au moins un résultat
            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()){
                    return true;
                }
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
        try(Connection connection=DatabaseManager.getConnection();
            PreparedStatement statement=connection.prepareStatement(query)){
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
            try(Connection connection=DatabaseManager.getConnection();
                PreparedStatement statement=connection.prepareStatement(query)){
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
        try(Connection connection=DatabaseManager.getConnection();
            PreparedStatement statement=connection.prepareStatement(query)){
            statement.setString(1, username);
            statement.setString(2, username);

            //Exécute la requête et vérifie s'il y a au moins un résultat
            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()){
                    return true;
                }
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
        try(Connection connection=DatabaseManager.getConnection();
            PreparedStatement statement=connection.prepareStatement(query)){
            statement.setString(1, username);

            //Exécute la requête et vérifie s'il y a au moins un résultat
            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()){
                    return resultSet.getInt(1);
                }
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
        try(Connection connection=DatabaseManager.getConnection();
            PreparedStatement statement=connection.prepareStatement(query)){
            statement.setString(1, email);

            //Exécute la requête et vérifie s'il y a au moins un résultat
            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()){
                    return resultSet.getString(1);
                }
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
        try(Connection connection=DatabaseManager.getConnection();
            PreparedStatement statement=connection.prepareStatement(query)){
            statement.setString(1, username);
            statement.setString(2, username);

            //Exécute la requête et vérifie s'il y a au moins un résultat
            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()){
                    return resultSet.getString(1);
                }
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
        try(Connection connection=DatabaseManager.getConnection();
            PreparedStatement statement=connection.prepareStatement(query)){
            statement.setString(1, username);
            statement.setString(2, username);

            //Exécute la requête et vérifie s'il y a au moins un résultat
            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()){
                    return resultSet.getInt(1);
                }
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

        try(Connection connection=DatabaseManager.getConnection();
            PreparedStatement statement=connection.prepareStatement(query)){

            int playerId=getPlayerId(username);
            statement.setInt(1, score);
            statement.setInt(2, playerId);

            //Exécute la requête et vérifie s'il y a au moins un résultat
            if(statement.executeUpdate()>0) return true;
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Error while selecting player.");
        }

        return false;
    }
}