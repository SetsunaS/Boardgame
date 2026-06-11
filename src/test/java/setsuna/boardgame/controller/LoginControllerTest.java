package setsuna.boardgame.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginControllerTest{
    /* Longueur du mot de passe */
    @Test
    void testPasswordTooSmall(){
        assertTrue(LoginController.isTooShort("short"));
    }

    @Test
    void testPasswordNotTooSmall(){
        assertFalse(LoginController.isTooShort("notshort"));
    }

    @Test
    void testPasswordLengthBoundary(){
        assertTrue(LoginController.isTooShort("123456"));
        assertFalse(LoginController.isTooShort("1234567"));
    }

    @Test
    void testEmptyPasswordIsTooSmall(){
        assertTrue(LoginController.isTooShort(""));
    }


    /* Majuscule */
    @Test
    void testPasswordWithUppercase(){
        assertTrue(LoginController.containsUpperCharacter("Upper"));
    }

    @Test
    void testPasswordWithoutUppercase(){
        assertFalse(LoginController.containsUpperCharacter("lower"));
    }

    @Test
    void testPasswordWithUppercaseAtTheEnd(){
        assertTrue(LoginController.containsUpperCharacter("lowerY"));
    }

    @Test
    void testEmptyPasswordHasNoUppercase(){
        assertFalse(LoginController.containsUpperCharacter(""));
    }


    /* Chiffre */
    @Test
    void testPasswordWithDigit(){
        assertTrue(LoginController.containsDigit("01digit"));
    }

    @Test
    void testPasswordWithoutDigit(){
        assertFalse(LoginController.containsDigit("nodigit"));
    }

    @Test
    void testPasswordWithDigitAtTheEnd(){
        assertTrue(LoginController.containsDigit("digit9"));
    }

    @Test
    void testEmptyPasswordHasNoDigit(){
        assertFalse(LoginController.containsDigit(""));
    }
}
