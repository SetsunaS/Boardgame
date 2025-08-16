package setsuna.boardgame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import setsuna.boardgame.database.DatabaseManager;
import setsuna.boardgame.model.general.Player;
import setsuna.boardgame.utils.Constants;
import setsuna.boardgame.utils.ViewChanger;
import setsuna.boardgame.utils.password.PasswordCrypt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginController implements GameController{
    @FXML
    private TextField loginUsernameTextField;

    @FXML
    private PasswordField loginPasswordField;

    @FXML
    private Label loginErrorMessageLabel;

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

    private Player currentPlayer;

    @Override
    public void setCurrentPlayer(Player player){
        currentPlayer=player;
    }

    public void login(ActionEvent actionEvent){
        //Efface tout précédent message d'erreur affiché
        hideErrorMessage(registerErrorMessageLabel);

        //Si tous les champs ne sont plus vides
        if(!isTextFieldEmpty(loginUsernameTextField) && !isTextFieldEmpty(loginPasswordField)){

            String username=loginUsernameTextField.getText();

            //Vérifie si username/email n'est pas dans la base de données
            if(!DatabaseManager.isUsernameInDatabase(username)){
                setAndDisplayErrorMessage(loginErrorMessageLabel, Constants.errorMessageLoginPasswordDontMatch);
            }

            else{
                //Vérifie si le champ username/email est un email ou pas (contient un @ ou non)
                if(!isUsername(username)){
                    username=DatabaseManager.getPlayerUsername(username);
                }

                //Vérifie le couple username/email & mot de passe
                if(!PasswordCrypt.checkPassword(loginPasswordField.getText(), DatabaseManager.getPlayerHashedPasword(username))){
                    //Affiche une erreur
                    setAndDisplayErrorMessage(loginErrorMessageLabel, Constants.errorMessageLoginPasswordDontMatch);
                }
                else{
                    //Création du Player
                    setCurrentPlayer(new Player(username, DatabaseManager.getScore(username)));

                    //Changement de scène pour aller au menu des jeux
                    ViewChanger.changeSceneToMenu(actionEvent, currentPlayer);
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
                        setCurrentPlayer(new Player(registerUsernameTextField.getText()));

                        //Changement de scène pour aller au menu des jeux
                        ViewChanger.changeSceneToMenu(actionEvent, currentPlayer);
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

        if(DatabaseManager.usernameAlreadyTaken(username)){
            setAndDisplayErrorMessage(errorMessageLabel, Constants.errorMessageUsernameAlreadyTaken);
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
            setAndDisplayErrorMessage(errorMessageLabel, Constants.errorMessageEmailNotValid);
            return false;
        }
        if(DatabaseManager.emailAlreadyTaken(email)){
            setAndDisplayErrorMessage(errorMessageLabel, Constants.errorMessageEmailAlreadyTaken);
            return false;
        }

        return true;
    }

    private static boolean isStrongPassword(PasswordField passwordField, Label errorMessageLabel){
        String password=passwordField.getText();

        if(isTooShort(password)){
            setAndDisplayErrorMessage(errorMessageLabel, Constants.errorMessagePasswordTooShort);
            return false;
        }

        if(!containsUpperCharacter(password)){
            setAndDisplayErrorMessage(errorMessageLabel, Constants.errorMessagePasswordWithoutUpperCharacter);
            return false;
        }

        if(!containsDigit(password)){
            setAndDisplayErrorMessage(errorMessageLabel, Constants.errorMessagePasswordWithoutDigit);
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
            setAndDisplayErrorMessage(registerErrorMessageLabel, Constants.errorMessagePasswordsDontMatch);
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