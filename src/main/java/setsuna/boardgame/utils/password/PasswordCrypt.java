package setsuna.boardgame.utils.password;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordCrypt{
    public static String hashPassword(String password){
        String salt=BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }

    public static boolean checkPassword(String password, String storedPassword){
        return BCrypt.checkpw(password, storedPassword);
    }
}