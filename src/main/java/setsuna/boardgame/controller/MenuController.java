package setsuna.boardgame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import setsuna.boardgame.utils.Constants;
import setsuna.boardgame.utils.network.Commands;
import setsuna.boardgame.utils.network.NetworkManager;
import setsuna.boardgame.model.general.player.HumanPlayer;
import setsuna.boardgame.model.general.player.Player;
import setsuna.boardgame.model.general.player.ai.TicTacToeAiPlayer;
import setsuna.boardgame.model.games.Games;
import setsuna.boardgame.utils.CustomAlert;
import setsuna.boardgame.utils.ViewChanger;
import java.util.List;

public class MenuController implements ControllerInterface{
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
    private Label joinErrorMessageLabel;

    @FXML
    private Label playerLabel;

    private Player currentPlayer;
    private Games selectedGame=null;
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
        selectedGame=Games.TIC_TAC_TOE;
        showGameConfiguration();
    }

    @FXML
    public void clickOnTestButton(ActionEvent actionEvent){
        selectedGame=Games.TEST;
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
            case TIC_TAC_TOE -> createLocalTicTacToe(actionEvent, loader, new HumanPlayer("X"));
            case TEST -> {}
        }
    }

    @FXML
    public void playWithAIButton(ActionEvent actionEvent){
        FXMLLoader loader=ViewChanger.changeSceneToGame(actionEvent, selectedGame, currentPlayer, networkManager);
        switch(selectedGame){
            case TIC_TAC_TOE -> createLocalTicTacToe(actionEvent, loader, new TicTacToeAiPlayer("AI"));
            case TEST -> {}
        }
    }

    @FXML
    public void createRoomButton(ActionEvent actionEvent){
        switch(selectedGame){
            case TIC_TAC_TOE -> createNetworkTicTacToe(actionEvent);
            case TEST -> {}
        }
    }

    @FXML
    public void joinRoomButton(ActionEvent actionEvent){
        switch(selectedGame){
            case TIC_TAC_TOE -> joinNetworkTicTacToe(actionEvent);
            case TEST -> {}
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

            List<Player> players=List.of(currentPlayer, secondPlayer);
            controller.createOfflineGameInterface(players, size);
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Cannot create local tic tac toe boardgame.");
        }
    }

    private void createNetworkTicTacToe(ActionEvent actionEvent){
        try{
            //Communication avec le serveur
            int size=getSize(3);
            networkManager.connectToServer();
            networkManager.sendMessageToServer(Commands.CREATE_ROOM+" "+Games.TIC_TAC_TOE+" "+size+" "+currentPlayer.getName());

            //Réception de la réponse du serveur
            int roomId=Integer.parseInt(networkManager.receiveMessageFromServer());

            //Création du plateau de jeu
            if(roomId!=-1){
                FXMLLoader loader=ViewChanger.changeSceneToGame(actionEvent, selectedGame, currentPlayer, networkManager);
                TicTacToeController controller=loader.getController();
                controller.createOnlineGameInterface(roomId);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Cannot create network tic tac toe boardgame.");
        }
    }

    private void joinNetworkTicTacToe(ActionEvent actionEvent){
        joinErrorMessageLabel.setVisible(false);

        try{
            //Communication avec le serveur
            int roomId=getRoomNumber();
            networkManager.connectToServer();
            networkManager.sendMessageToServer(Commands.JOIN_ROOM+" "+roomId+" "+currentPlayer.getName());

            //Réception de la réponse du serveur
            boolean isJoin=Boolean.parseBoolean(networkManager.receiveMessageFromServer());

            //Création du plateau de jeu
            if(isJoin){
                FXMLLoader loader=ViewChanger.changeSceneToGame(actionEvent, selectedGame, currentPlayer, networkManager);
                TicTacToeController controller=loader.getController();
                controller.createOnlineGameInterface(roomId);
            }
            else{
                joinErrorMessageLabel.setText(Constants.ERROR_MESSAGE_JOIN_ROOM);
                joinErrorMessageLabel.setVisible(true);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Cannot join tic tac toe boardgame.");
        }
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