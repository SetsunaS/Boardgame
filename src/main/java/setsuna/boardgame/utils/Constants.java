package setsuna.boardgame.utils;

public class Constants{
    //Path
    public static final String loginViewPath="LoginView/LoginView.fxml";
    public static final String menuViewPath="MenuView/MenuView.fxml";
    public static final String ticTacToeViewPath="TicTacToeView/TicTacToeView.fxml";
    public static final String customAlertViewPath="utils/CustomAlertView.fxml";
    public static final String databasePropertiesPath="annexes/Database/database.properties";

    //Register error
    public static final String errorMessageUsernameAlreadyTaken="This username is already in use.";
    public static final String errorMessageEmailNotValid="The email address you entered is not valid.";
    public static final String errorMessageEmailAlreadyTaken="This email is already in use.";
    public static final String errorMessagePasswordTooShort="Password too weak: please choose a password with at least 8 characters.";
    public static final String errorMessagePasswordWithoutUpperCharacter="Password too weak: your password must contain at least one uppercase letter.";
    public static final String errorMessagePasswordWithoutDigit="Password too weak: your password must contain at least one digit.";
    public static final String errorMessagePasswordsDontMatch="Passwords do not match.";

    //Login error
    public static final String errorMessageLoginPasswordDontMatch="Incorrect username/email or password.";
    private static final String password="MySecretWord01";
}