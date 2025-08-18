package setsuna.boardgame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import setsuna.boardgame.controller.network.NetworkManager;
import setsuna.boardgame.model.general.player.HumanPlayer;
import setsuna.boardgame.model.general.player.Player;
import setsuna.boardgame.model.general.player.ai.TicTacToeAiPlayer;
import setsuna.boardgame.model.games.GamesEnum;
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
    private TextField gameNumberField;

    @FXML
    private Label playerLabel;

    private Player currentPlayer;
    private GamesEnum selectedGame=null;
    private NetworkManager networkManager;


    @Override
    public void setCurrentPlayer(Player player){
        currentPlayer=player;
        playerLabel.setText(player.getName()+": "+player.getScore());
    }

    @Override
    public void setNetworkManager(NetworkManager networkManager){
        this.networkManager=networkManager;
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
    public void playOnLocalButton(ActionEvent actionEvent){
        FXMLLoader loader=ViewChanger.changeSceneToGame(actionEvent, selectedGame, currentPlayer, networkManager);
        switch(selectedGame){
            case TicTacToe -> createLocalTicTacToe(actionEvent, loader, new HumanPlayer("X"));
            case Test -> {}
        }
    }

    @FXML
    public void playWithAIButton(ActionEvent actionEvent){
        FXMLLoader loader=ViewChanger.changeSceneToGame(actionEvent, selectedGame, currentPlayer, networkManager);
        switch(selectedGame){
            case TicTacToe -> createLocalTicTacToe(actionEvent, loader, new TicTacToeAiPlayer("AI"));
            case Test -> {}
        }
    }

    @FXML
    public void createRoomButton(ActionEvent actionEvent){
        FXMLLoader loader=ViewChanger.changeSceneToGame(actionEvent, selectedGame, currentPlayer, networkManager);
        switch(selectedGame){
            case TicTacToe -> createNetworkTicTacToe(actionEvent, loader);
            case Test -> {}
        }
    }

    @FXML
    public void joinRoomButton(ActionEvent actionEvent){
        FXMLLoader loader=ViewChanger.changeSceneToGame(actionEvent, selectedGame, currentPlayer, networkManager);
        switch(selectedGame){
            case TicTacToe -> joinNetworkTicTacToe(actionEvent, loader);
            case Test -> {}
        }
    }

    private int getSize(int defaultSize){
        int size=defaultSize;
        try{
            size=Integer.parseInt(gameSizeField.getText());
        }
        catch(Exception e){}
        return size;
    }

    private int getRoomNumber(){
        try{
            return Integer.parseInt(gameNumberField.getText());
        }
        catch(Exception e){}
        return -1;
    }

    private void createLocalTicTacToe(ActionEvent actionEvent, FXMLLoader loader, Player secondPlayer){
        //Créer la grille et ajoute les joueurs
        try{
            int size=getSize(3);
            TicTacToeController controller=loader.getController();
            controller.createGameInterface(size, false);

            controller.addPlayer(secondPlayer);
            secondPlayer.setGame(controller.getGame());
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Cannot create local tic tac toe boardgame.");
        }
    }

    private void createNetworkTicTacToe(ActionEvent actionEvent, FXMLLoader loader){
        try{
            //Création du plateau de jeu
            int size=getSize(3);
            TicTacToeController controller=loader.getController();
            controller.createGameInterface(size, true);

            //Communication avec le serveur
            networkManager.connectToServer();
            networkManager.sendMessageToServer("CREATE_ROOM TIC_TAC_TOE "+size+" 2 "+currentPlayer.getName());

            //Réception de la réponse du serveur
            int roomId=Integer.parseInt(networkManager.receiveMessageFromServer());
            if(roomId!=-1) controller.setRoomId(roomId);
            networkManager.closeConnection();
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Cannot create network tic tac toe boardgame.");
        }
    }

    private void joinNetworkTicTacToe(ActionEvent actionEvent, FXMLLoader loader){
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
            if(customAlert.getResult()) ViewChanger.changeSceneToLogin(actionEvent, currentPlayer, networkManager);
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Cannot create custom alert confirmation for log out.");
        }
    }
}