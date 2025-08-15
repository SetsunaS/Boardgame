package setsuna.boardgame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import setsuna.boardgame.model.general.Player;
import setsuna.boardgame.utils.Constants;
import setsuna.boardgame.utils.ViewChanger;

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
        //TODO: Récupération du textfield username, et vérifie si c'est un email ou pas (contient un @ ou non)
        boolean isUsername=true;


        //TODO: Vérification du couple username/email & mot de passe

        //TODO: Affichage des erreurs
        //loginMessageLabel.setVisible(true);

        //TODO: Création du Player


        //Changement de scène pour aller au menu des jeux
        ViewChanger.changeSceneToMenu(actionEvent, currentPlayer);
    }

    public void register(ActionEvent actionEvent){
        //Efface tout précédent message d'erreur affiché
        hideErrorMessage(true);

        //Vérifie la robustesse du mot de passe et affiche les erreurs
        isStrongPassword(registerPasswordField.getText());

        //TODO: Vérification de non doublon d'username ou email dans la base de données et traitement d'erreurs
        //TODO: Envoie de lien email pour confirmer l'email
        //TODO: Une fois l'email vérifié, ajouter dans la base de données
        //TODO: Création du Player


        //Changement de scène pour aller au menu des jeux
        //ViewChanger.changeSceneToMenu(actionEvent, currentPlayer);
    }

    private boolean isStrongPassword(String password){
        if(isTooShort(password)){
            setAndDisplayErrorMessage(true, Constants.errorMessagePasswordTooShort);
            return false;
        }

        if(!containsUpperCharacter(password)){
            setAndDisplayErrorMessage(true, Constants.errorMessagePasswordWithoutUpperCharacter);
            return false;
        }

        if(!containsDigit(password)){
            setAndDisplayErrorMessage(true, Constants.errorMessagePasswordWithoutDigit);
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

    private void setAndDisplayErrorMessage(boolean isRegister, String message){
        if(isRegister){
            registerErrorMessageLabel.setText(message);
            registerErrorMessageLabel.setVisible(true);
        }
        else{
            loginErrorMessageLabel.setText(message);
            loginErrorMessageLabel.setVisible(true);
        }
    }

    private void hideErrorMessage(boolean isRegister){
        if(isRegister) registerErrorMessageLabel.setVisible(false);
        else loginErrorMessageLabel.setVisible(false);
    }
}