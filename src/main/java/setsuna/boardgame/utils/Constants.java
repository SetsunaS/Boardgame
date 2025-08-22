package setsuna.boardgame.utils;

public class Constants{
    //Path
    public static final String LOGIN_VIEW_PATH="LoginView/LoginView.fxml";
    public static final String MENU_VIEW_PATH="MenuView/MenuView.fxml";
    public static final String TIC_TAC_TOE_VIEW_PATH="TicTacToeView/TicTacToeView.fxml";
    public static final String CUSTOM_ALERT_VIEW_PATH="utils/CustomAlertView.fxml";
    public static final String DATABASE_PROPERTIES_PATH="annexes/Database/database.properties";


    //Register error
    public static final String ERROR_MESSAGE_USERNAME_ALREADY_TAKEN="This username is already in use.";
    public static final String ERROR_MESSAGE_EMAIL_NOT_VALID="The email address you entered is not valid.";
    public static final String ERROR_MESSAGE_EMAIL_ALREADY_TAKEN="This email is already in use.";
    public static final String ERROR_MESSAGE_PASSWORD_TOO_SHORT="Password too weak: please choose a password with at least 8 characters.";
    public static final String ERROR_MESSAGE_PASSWORD_WITHOUT_UPPER_CHARACTER="Password too weak: your password must contain at least one uppercase letter.";
    public static final String ERROR_MESSAGE_PASSWORD_WITHOUT_DIGIT="Password too weak: your password must contain at least one digit.";
    public static final String ERROR_MESSAGE_PASSWORDS_DONT_MATCH="Passwords do not match.";

    //Login error
    public static final String ERROR_MESSAGE_LOGIN_PASSWORD_DONT_MATCH="Incorrect username/email or password.";


    //Serveur
    public static final String SERVER_HOST="127.0.0.1";
    public static final int SERVER_PORT=7_777;
    public static final String ERROR_MESSAGE_JOIN_ROOM="Error while joining the room.";

    private static final String password="MySecretWord01";
}