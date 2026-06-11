package setsuna.boardgame.utils.password;

import org.junit.jupiter.api.Test;
import setsuna.boardgame.utils.Constants;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordValidatorTest{
    /* Longueur du mot de passe */
    @Test
    void testPasswordTooSmall(){
        assertTrue(PasswordValidator.isTooShort("short"));
    }

    @Test
    void testPasswordNotTooSmall(){
        assertFalse(PasswordValidator.isTooShort("notshort"));
    }

    @Test
    void testPasswordLengthBoundary(){
        assertTrue(PasswordValidator.isTooShort("123456"));
        assertFalse(PasswordValidator.isTooShort("1234567"));
    }

    @Test
    void testEmptyPasswordIsTooSmall(){
        assertTrue(PasswordValidator.isTooShort(""));
    }


    /* Majuscule */
    @Test
    void testPasswordWithUppercase(){
        assertTrue(PasswordValidator.containsUpperCharacter("Upper"));
    }

    @Test
    void testPasswordWithoutUppercase(){
        assertFalse(PasswordValidator.containsUpperCharacter("lower"));
    }

    @Test
    void testPasswordWithUppercaseAtTheEnd(){
        assertTrue(PasswordValidator.containsUpperCharacter("lowerY"));
    }

    @Test
    void testEmptyPasswordHasNoUppercase(){
        assertFalse(PasswordValidator.containsUpperCharacter(""));
    }


    /* Chiffre */
    @Test
    void testPasswordWithDigit(){
        assertTrue(PasswordValidator.containsDigit("01digit"));
    }

    @Test
    void testPasswordWithoutDigit(){
        assertFalse(PasswordValidator.containsDigit("nodigit"));
    }

    @Test
    void testPasswordWithDigitAtTheEnd(){
        assertTrue(PasswordValidator.containsDigit("digit9"));
    }

    @Test
    void testEmptyPasswordHasNoDigit(){
        assertFalse(PasswordValidator.containsDigit(""));
    }


    /* Message d'erreur global */
    @Test
    void testStrongPasswordHasNoError(){
        assertNull(PasswordValidator.getPasswordError("Password01"));
    }

    @Test
    void testTooShortPasswordError(){
        assertEquals(Constants.ERROR_MESSAGE_PASSWORD_TOO_SHORT, PasswordValidator.getPasswordError("Sh0rt"));
    }

    @Test
    void testNoUppercasePasswordError(){
        assertEquals(Constants.ERROR_MESSAGE_PASSWORD_WITHOUT_UPPER_CHARACTER, PasswordValidator.getPasswordError("password01"));
    }

    @Test
    void testNoDigitPasswordError(){
        assertEquals(Constants.ERROR_MESSAGE_PASSWORD_WITHOUT_DIGIT, PasswordValidator.getPasswordError("PasswordAB"));
    }
}
