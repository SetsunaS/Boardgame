package setsuna.boardgame.utils.password;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordCryptTest{
    @Test
    void testHashIsNotPlainPassword(){
        String hash=PasswordCrypt.hashPassword("Password01");
        assertNotEquals("Password01", hash);
    }

    @Test
    void testCheckPasswordWithCorrectPassword(){
        String hash=PasswordCrypt.hashPassword("Password01");
        assertTrue(PasswordCrypt.checkPassword("Password01", hash));
    }

    @Test
    void testCheckPasswordWithWrongPassword(){
        String hash=PasswordCrypt.hashPassword("Password01");
        assertFalse(PasswordCrypt.checkPassword("WrongPassword01", hash));
    }

    @Test
    void testSamePasswordGivesDifferentHashes(){
        //Le sel doit rendre chaque hash unique
        String firstHash=PasswordCrypt.hashPassword("Password01");
        String secondHash=PasswordCrypt.hashPassword("Password01");

        assertNotEquals(firstHash, secondHash);
        assertTrue(PasswordCrypt.checkPassword("Password01", firstHash));
        assertTrue(PasswordCrypt.checkPassword("Password01", secondHash));
    }
}
