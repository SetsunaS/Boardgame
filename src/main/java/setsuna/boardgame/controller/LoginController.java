package setsuna.boardgame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import setsuna.boardgame.utils.network.NetworkManager;
import setsuna.boardgame.utils.database.DatabaseManager;
import setsuna.boardgame.model.general.player.HumanPlayer;
import setsuna.boardgame.model.general.player.Player;
import setsuna.boardgame.utils.Constants;
import setsuna.boardgame.utils.ViewChanger;
import setsuna.boardgame.utils.password.PasswordCrypt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginController implements IController{
    @FXML
    private TabPane tabPane;

    @FXML
    private Tab loginTab;

    @FXML
    private TextField loginUsernameTextField;

    @FXML
    private PasswordField loginPasswordField;

    @FXML
    private Label loginErrorMessageLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Tab registerTab;

    @FXML
    private TextField registerUsernameTextField;

    @FXML
    private TextField registerEmailTextField;

    @FXML
    private PasswordField registerPasswordField;

    @FXML
    private PasswordField registerVerifyPasswordField;

    @FXML
    private Label registerErrorMessageLabel;

    @FXML
    private Button registerButton;

    private Player currentPlayer;
    private NetworkManager networkManager;

    @Override
    public void setCurrentPlayer(Player player){
        currentPlayer=player;
    }

    @Override
    public void setNetworkManager(NetworkManager networkManager){
        this.networkManager=networkManager;
    }

    public void initialize(){
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if(newTab==loginTab){
                registerButton.setDefaultButton(false);
                loginButton.setDefaultButton(true);
            }
            else if(newTab==registerTab){
                loginButton.setDefaultButton(false);
                registerButton.setDefaultButton(true);
            }
        });

        loginButton.setDefaultButton(true);
    }

    public void login(ActionEvent actionEvent){
        //Efface tout précédent message d'erreur affiché
        hideErrorMessage(registerErrorMessageLabel);

        //Si tous les champs ne sont plus vides
        if(!isTextFieldEmpty(loginUsernameTextField) && !isTextFieldEmpty(loginPasswordField)){

            String username=loginUsernameTextField.getText();

            //Vérifie si username/email n'est pas dans la base de données
            if(!DatabaseManager.isUsernameInDatabase(username)){
                setAndDisplayErrorMessage(loginErrorMessageLabel, Constants.ERROR_MESSAGE_LOGIN_PASSWORD_DONT_MATCH);
            }

            else{
                //Vérifie si le champ username/email est un email ou pas (contient un @ ou non)
                if(!isUsername(username)){
                    username=DatabaseManager.getPlayerUsername(username);
                }

                //Vérifie le couple username/email & mot de passe
                if(!PasswordCrypt.checkPassword(loginPasswordField.getText(), DatabaseManager.getPlayerHashedPasword(username))){
                    //Affiche une erreur
                    setAndDisplayErrorMessage(loginErrorMessageLabel, Constants.ERROR_MESSAGE_LOGIN_PASSWORD_DONT_MATCH);
                }
                else{
                    //Création du Player
                    setCurrentPlayer(new HumanPlayer(username, DatabaseManager.getScore(username)));

                    //Création du canal de communication
                    setNetworkManager(new NetworkManager());

                    //Changement de scène pour aller au menu des jeux
                    ViewChanger.changeSceneToMenu(actionEvent, currentPlayer, networkManager);
                }
            }
        }
    }

    private static boolean isUsername(String username){
        return !username.contains("@");
    }


    public void register(ActionEvent actionEvent){
        //Efface tout précédent message d'erreur affiché
        hideErrorMessage(registerErrorMessageLabel);

        //Si tous les champs ne sont plus vides
        if(!isTextFieldEmpty(registerUsernameTextField) && !isTextFieldEmpty(registerEmailTextField)
        && !isTextFieldEmpty(registerPasswordField) && !isTextFieldEmpty(registerVerifyPasswordField)){

            //Vérifie l'username et l'email dans la base de données et affiche les erreurs
            if(isValidUsername(registerUsernameTextField, registerErrorMessageLabel)
            && isValidEmail(registerEmailTextField, registerErrorMessageLabel)){

                //Vérifie les mots de passe et affiche les erreurs
                if(isStrongPassword(registerPasswordField, registerErrorMessageLabel)
                && isSamePassword(registerPasswordField, registerVerifyPasswordField, registerErrorMessageLabel)){

                    if(DatabaseManager.insertNewPlayer(registerUsernameTextField.getText(), registerEmailTextField.getText(), PasswordCrypt.hashPassword(registerPasswordField.getText()))
                    && DatabaseManager.insertNewScore(registerUsernameTextField.getText())){
                        //Création du Player
                        setCurrentPlayer(new HumanPlayer(registerUsernameTextField.getText()));

                        //Création du canal de communication
                        setNetworkManager(new NetworkManager());

                        //Changement de scène pour aller au menu des jeux
                        ViewChanger.changeSceneToMenu(actionEvent, currentPlayer, networkManager);
                    }
                }
            }
        }
    }

    private static boolean isTextFieldEmpty(TextField textField){
        return textField.getText().length()==0;
    }

    private static boolean isValidUsername(TextField usernameTextField, Label errorMessageLabel){
        String username=usernameTextField.getText();

        if(!username.matches("[a-zA-Z0-9]+") || username.equals("null")){
            setAndDisplayErrorMessage(errorMessageLabel, Constants.ERROR_MESSAGE_USERNAME_NOT_VALID);
            return false;
        }

        if(DatabaseManager.usernameAlreadyTaken(username)){
            setAndDisplayErrorMessage(errorMessageLabel, Constants.ERROR_MESSAGE_USERNAME_ALREADY_TAKEN);
            return false;
        }

        return true;
    }

    private static boolean isValidEmail(TextField emailTextField, Label errorMessageLabel){
        String email=emailTextField.getText();
        String regex="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(email);

        if(!matcher.matches()){
            setAndDisplayErrorMessage(errorMessageLabel, Constants.ERROR_MESSAGE_EMAIL_NOT_VALID);
            return false;
        }
        if(DatabaseManager.emailAlreadyTaken(email)){
            setAndDisplayErrorMessage(errorMessageLabel, Constants.ERROR_MESSAGE_EMAIL_ALREADY_TAKEN);
            return false;
        }

        return true;
    }

    private static boolean isStrongPassword(PasswordField passwordField, Label errorMessageLabel){
        String password=passwordField.getText();

        if(isTooShort(password)){
            setAndDisplayErrorMessage(errorMessageLabel, Constants.ERROR_MESSAGE_PASSWORD_TOO_SHORT);
            return false;
        }

        if(!containsUpperCharacter(password)){
            setAndDisplayErrorMessage(errorMessageLabel, Constants.ERROR_MESSAGE_PASSWORD_WITHOUT_UPPER_CHARACTER);
            return false;
        }

        if(!containsDigit(password)){
            setAndDisplayErrorMessage(errorMessageLabel, Constants.ERROR_MESSAGE_PASSWORD_WITHOUT_DIGIT);
            return false;
        }

        return true;
    }

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

    private static boolean isSamePassword(PasswordField passwordField, PasswordField secondPasswordField, Label registerErrorMessageLabel){
        if(!passwordField.getText().equals(secondPasswordField.getText())){
            setAndDisplayErrorMessage(registerErrorMessageLabel, Constants.ERROR_MESSAGE_PASSWORDS_DONT_MATCH);
            return false;
        }
        return true;
    }

    private static void setAndDisplayErrorMessage(Label errorMessageLabel, String message){
        errorMessageLabel.setText(message);
        errorMessageLabel.setVisible(true);
    }

    private void hideErrorMessage(Label errorMessageLabel){
        errorMessageLabel.setVisible(false);
    }
}