package setsuna.boardgame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import setsuna.boardgame.GameApplication;
import setsuna.boardgame.model.games.GamesEnum;
import setsuna.boardgame.model.general.Player;

import java.io.IOException;

public class MenuController{
    @FXML
    private StackPane rootPane;

    @FXML
    private FlowPane gameSelectionPane;

    @FXML
    private Button ticTacToeButton;

    @FXML
    private Button testButton;

    @FXML
    private Region blurOverlay;

    @FXML
    private VBox gameConfigurationPane;

    @FXML
    private TextField gameSizeField;

    @FXML
    private Button playWithAIButton;

    @FXML
    private Button createRoomButton;

    @FXML
    private TextField gameNumberField;

    @FXML
    private Button joinRoomButton;

    @FXML
    private Button cancelButton;

    private GamesEnum selectedGame=null;

    private Player currentPlayer;

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
        try{
            //Scène à changer
            Scene currentScene=((Node)actionEvent.getSource()).getScene();

            //Scène à mettre
            FXMLLoader loader=new FXMLLoader(GameApplication.class.getResource("TicTacToeView/TicTacToeView.fxml"));
            Parent newRoot=loader.load();

            //Initialiser la taille de la grille
            int size=3;
            try{
                size=Integer.valueOf(gameSizeField.getText());
            }
            catch(Exception e){}

            TicTacToeController ticTacToeController=loader.getController();
            ticTacToeController.createGameInterface(size);

            //Changement de scène
            currentScene.setRoot(newRoot);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void createRoom(ActionEvent actionEvent){
        //TODO
    }

    @FXML
    public void joinRoom(ActionEvent actionEvent){
        //TODO
    }
}