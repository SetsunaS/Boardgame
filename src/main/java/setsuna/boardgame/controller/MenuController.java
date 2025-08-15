package setsuna.boardgame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import setsuna.boardgame.model.games.GamesEnum;
import setsuna.boardgame.model.general.Player;
import setsuna.boardgame.utils.CustomAlert;
import setsuna.boardgame.utils.ViewChanger;

public class MenuController implements GameController{
    @FXML
    private FlowPane gameSelectionPane;

    @FXML
    private Region blurOverlay;

    @FXML
    private VBox gameConfigurationPane;

    @FXML
    private TextField gameSizeField;

    @FXML
    private TextField gameNumberField; //TODO

    @FXML
    private Label playerLabel; //TODO

    private GamesEnum selectedGame=null;

    private Player currentPlayer;

    @Override
    public void setCurrentPlayer(Player player){
        currentPlayer=player;
    }


    @FXML
    public void clickOnTicTacToeButton(ActionEvent actionEvent){
        selectedGame=GamesEnum.TicTacToe;
        showGameConfiguration();
    }

    @FXML
    public void clickOnTestButton(ActionEvent actionEvent){
        selectedGame=GamesEnum.Test;
        showGameConfiguration();
    }

    public void showGameConfiguration(){
        //Effet de flou sur le menu
        gameSelectionPane.setEffect(new GaussianBlur(10));
        blurOverlay.setVisible(true);
        blurOverlay.setManaged(true);

        //Affiche le choix pour créer un jeu
        gameConfigurationPane.setVisible(true);
        gameConfigurationPane.setManaged(true);
    }

    @FXML
    public void hideGameConfiguration(ActionEvent actionEvent){
        selectedGame=null;

        //Enlève l'effet de flou sur le menu
        gameSelectionPane.setEffect(null);
        blurOverlay.setVisible(false);
        blurOverlay.setManaged(false);

        //Efface le choix pour créer un jeu
        gameConfigurationPane.setVisible(false);
        gameConfigurationPane.setManaged(false);
    }

    @FXML
    public void playWithAIButton(ActionEvent actionEvent){
        FXMLLoader loader=ViewChanger.changeSceneToTicTacToe(actionEvent, currentPlayer);

        //Initialiser la taille de la grille, par défaut à 3
        int size=3;
        try{
            size=Integer.parseInt(gameSizeField.getText());
        }
        catch(Exception e){}

        try{
            ((TicTacToeController)loader.getController()).createGameInterface(size);
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Impossible de créer le plateau de jeu Morpion");
        }
    }

    @FXML
    public void createGameButton(ActionEvent actionEvent){
        //TODO
    }

    @FXML
    public void joinRoomButton(ActionEvent actionEvent){
        //TODO
    }

    @FXML
    public void logout(ActionEvent actionEvent){
        try{
            //Création et affichage de la pop-up de demande de confirmation
            Stage newStage=new Stage();
            CustomAlert customAlert=ViewChanger.createAlert("Confirmation", "Log out?", (Stage)gameSelectionPane.getScene().getWindow(), newStage);

            //Attente d'une réponse et traitement
            newStage.showAndWait(); //attend que la popup soit fermée
            if(customAlert.getResult()) ViewChanger.changeSceneToLogin(actionEvent, currentPlayer);
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Cannot create custom alert confirmation for log out.");
        }
    }
}