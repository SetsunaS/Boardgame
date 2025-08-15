import org.junit.jupiter.api.Test;
import setsuna.boardgame.controller.LoginController;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginControllerTest{
    @Test
    void testPasswordTooSmall(){
        boolean result=LoginController.isTooShort("short");
        assertTrue(result);
    }

    @Test
    void testPasswordNotTooSmall(){
        boolean result=LoginController.isTooShort("notshort");
        assertFalse(result);
    }

    @Test
    void testPasswordWithUppercase(){
        boolean result=LoginController.containsUpperCharacter("Upper");
        assertTrue(result);
    }

    @Test
    void testPasswordWithoutUppercase(){
        boolean result=LoginController.containsUpperCharacter("lower");
        assertFalse(result);
    }

    @Test
    void testPasswordWithDigit(){
        boolean result=LoginController.containsDigit("01digit");
        assertTrue(result);
    }

    @Test
    void testPasswordWithoutDigit(){
        boolean result=LoginController.containsDigit("nodigit");
        assertFalse(result);
    }
}