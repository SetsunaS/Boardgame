package setsuna.boardgame.utils.database;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import setsuna.boardgame.utils.password.PasswordCrypt;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers(disabledWithoutDocker=true)
public class DatabaseManagerTest{
    @Container
    private static final PostgreSQLContainer<?> postgres=new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("boardgame");

    @BeforeAll
    static void initSchema() throws Exception{
        //Exécute le script d'initialisation du projet, sans le CREATE DATABASE (le conteneur crée déjà la base)
        String script;
        try(var scriptStream=DatabaseManagerTest.class.getResourceAsStream("/setsuna/boardgame/annexes/Database/database_initialisation.sql")){
            script=new String(scriptStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        script=script.replaceFirst("(?i)CREATE DATABASE[^;]*;", "");

        try(Connection connection=DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
            Statement statement=connection.createStatement()){
            statement.execute(script);
        }

        //Pointe DatabaseManager vers la base du conteneur
        DatabaseManager.overrideConnectionConfiguration(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    }

    @BeforeEach
    void cleanTables() throws Exception{
        try(Connection connection=DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
            Statement statement=connection.createStatement()){
            statement.execute("TRUNCATE player RESTART IDENTITY CASCADE");
        }
    }

    private static final String ALICE_HASH=PasswordCrypt.hashPassword("Password01");

    private void insertAlice(){
        assertTrue(DatabaseManager.insertNewPlayer("alice", "alice@example.com", ALICE_HASH));
    }


    /* Insertion de joueurs */
    @Test
    void testInsertNewPlayer(){
        insertAlice();
        assertTrue(DatabaseManager.usernameAlreadyTaken("alice"));
    }

    @Test
    void testInsertDuplicateUsernameFails(){
        insertAlice();
        assertFalse(DatabaseManager.insertNewPlayer("alice", "other@example.com", ALICE_HASH));
    }

    @Test
    void testInsertDuplicateEmailFails(){
        insertAlice();
        assertFalse(DatabaseManager.insertNewPlayer("bob", "alice@example.com", ALICE_HASH));
    }


    /* Unicité username/email */
    @Test
    void testUsernameAlreadyTaken(){
        assertFalse(DatabaseManager.usernameAlreadyTaken("alice"));
        insertAlice();
        assertTrue(DatabaseManager.usernameAlreadyTaken("alice"));
        assertFalse(DatabaseManager.usernameAlreadyTaken("bob"));
    }

    @Test
    void testEmailAlreadyTaken(){
        assertFalse(DatabaseManager.emailAlreadyTaken("alice@example.com"));
        insertAlice();
        assertTrue(DatabaseManager.emailAlreadyTaken("alice@example.com"));
        assertFalse(DatabaseManager.emailAlreadyTaken("bob@example.com"));
    }

    @Test
    void testIsUsernameInDatabase(){
        insertAlice();

        //Recherche par username ou par email
        assertTrue(DatabaseManager.isUsernameInDatabase("alice"));
        assertTrue(DatabaseManager.isUsernameInDatabase("alice@example.com"));
        assertFalse(DatabaseManager.isUsernameInDatabase("bob"));
    }


    /* Lecture des informations d'un joueur */
    @Test
    void testGetPlayerId(){
        insertAlice();
        assertTrue(DatabaseManager.getPlayerId("alice")>0);
        assertEquals(-1, DatabaseManager.getPlayerId("bob"));
    }

    @Test
    void testGetPlayerUsername(){
        insertAlice();
        assertEquals("alice", DatabaseManager.getPlayerUsername("alice@example.com"));
        assertNull(DatabaseManager.getPlayerUsername("bob@example.com"));
    }

    @Test
    void testGetPlayerHashedPasword(){
        insertAlice();

        //Le hash récupéré doit valider le mot de passe d'origine
        String storedHash=DatabaseManager.getPlayerHashedPasword("alice");
        assertTrue(PasswordCrypt.checkPassword("Password01", storedHash));

        //Recherche par email aussi
        assertEquals(storedHash, DatabaseManager.getPlayerHashedPasword("alice@example.com"));

        assertNull(DatabaseManager.getPlayerHashedPasword("bob"));
    }


    /* Scores */
    @Test
    void testInsertNewScoreDefaultsToZero(){
        insertAlice();
        assertTrue(DatabaseManager.insertNewScore("alice"));
        assertEquals(0, DatabaseManager.getScore("alice"));
    }

    @Test
    void testInsertNewScoreWithValue(){
        insertAlice();
        assertTrue(DatabaseManager.insertNewScore("alice", 30));
        assertEquals(30, DatabaseManager.getScore("alice"));
    }

    @Test
    void testInsertNewScoreForUnknownPlayer(){
        assertFalse(DatabaseManager.insertNewScore("bob", 10));
    }

    @Test
    void testGetScoreByEmail(){
        insertAlice();
        DatabaseManager.insertNewScore("alice", 20);
        assertEquals(20, DatabaseManager.getScore("alice@example.com"));
    }

    @Test
    void testGetScoreForUnknownPlayer(){
        assertEquals(-1, DatabaseManager.getScore("bob"));
    }

    @Test
    void testUpdatePlayerScore(){
        insertAlice();
        DatabaseManager.insertNewScore("alice");

        assertTrue(DatabaseManager.updatePlayerScore("alice", 42));
        assertEquals(42, DatabaseManager.getScore("alice"));
    }

    @Test
    void testUpdatePlayerScoreForUnknownPlayer(){
        assertFalse(DatabaseManager.updatePlayerScore("bob", 42));
    }
}
