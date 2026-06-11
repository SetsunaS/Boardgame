package setsuna.boardgame.utils.password;

import setsuna.boardgame.utils.Constants;

public class PasswordValidator{
    public static boolean isTooShort(String word){
        return word.length()<7;
    }

    public static boolean containsUpperCharacter(String word){
        for(int i=0; i<word.length(); i++)
            if(Character.isUpperCase(word.charAt(i))) return true;
        return false;
    }

    public static boolean containsDigit(String word){
        for(int i=0; i<word.length(); i++)
            if(Character.isDigit(word.charAt(i))) return true;
        return false;
    }

    /** Retourne le message d'erreur, ou null si le mot de passe est assez fort **/
    public static String getPasswordError(String password){
        if(isTooShort(password)) return Constants.ERROR_MESSAGE_PASSWORD_TOO_SHORT;
        if(!containsUpperCharacter(password)) return Constants.ERROR_MESSAGE_PASSWORD_WITHOUT_UPPER_CHARACTER;
        if(!containsDigit(password)) return Constants.ERROR_MESSAGE_PASSWORD_WITHOUT_DIGIT;
        return null;
    }
}
