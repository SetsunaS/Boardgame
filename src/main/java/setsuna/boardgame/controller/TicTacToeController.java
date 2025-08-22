package setsuna.boardgame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import setsuna.boardgame.model.general.player.HumanPlayer;
import setsuna.boardgame.utils.network.Commands;
import setsuna.boardgame.utils.network.NetworkManager;
import setsuna.boardgame.model.general.player.Player;
import setsuna.boardgame.model.general.player.ai.TicTacToeAiPlayer;
import setsuna.boardgame.model.games.TicTacToe;
import setsuna.boardgame.model.general.Pawn;
import setsuna.boardgame.model.general.exception.InvalidMoveException;
import setsuna.boardgame.model.general.exception.PlayerFullException;
import setsuna.boardgame.utils.CustomAlert;
import setsuna.boardgame.utils.ViewChanger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TicTacToeController implements ControllerInterface, GameControllerInterface{
    @FXML
    private StackPane rootPane;

    @FXML
    private VBox allContentVBox;

    @FXML
    private HBox headerInformation;

    @FXML
    private Label playerNameLabel;

    @FXML
    private GridPane ticTacToeGridPane;

    @FXML
    private Region blurOverlay;

    @FXML
    private HBox footerInformation;

    @FXML
    private Label roomIdLabel;

    @FXML
    private VBox gameWinnerAnnounce;

    @FXML
    private Label gameWinnerLabel;

    private Player currentPlayer;
    private TicTacToe game;
    private boolean isOnline;
    private int roomId;
    private List<Button> buttons=new ArrayList<>();
    private NetworkManager networkManager;

    @Override
    public void setCurrentPlayer(Player player){
        currentPlayer=player;
    }

    @Override
    public void setNetworkManager(NetworkManager networkManager){
        this.networkManager=networkManager;
    }

    public TicTacToe getGame(){
        return game;
    }

    @FXML
    private void initialize(){
        //Fixe la taille de la grille de jeu selon la taille la plus petite de largeur/longueur
        ticTacToeGridPane.maxWidthProperty().bind(rootPane.heightProperty().multiply(0.75));
        ticTacToeGridPane.maxHeightProperty().bind(rootPane.heightProperty().multiply(0.75));

        //Même longueur pour les lignes d'informations
        headerInformation.maxWidthProperty().bind(rootPane.heightProperty().multiply(0.75));
        footerInformation.maxWidthProperty().bind(rootPane.heightProperty().multiply(0.75));

        //Fixe les marges entre les cases
        ticTacToeGridPane.hgapProperty().bind(ticTacToeGridPane.heightProperty().multiply(0.01));
        ticTacToeGridPane.vgapProperty().bind(ticTacToeGridPane.heightProperty().multiply(0.01));
    }

    @Override
    public void createOfflineGameInterface(List<Player> players, int size){
        this.isOnline=false;

        //Gestion du model dans le controller si jeu en local
        this.game=new TicTacToe(size);

        //Joueurs
        for(Player player: players) addOfflinePlayer(player);
        updateCurrentPlayerName();

        //Création du plateau de jeu
        createBoard(size);
    }

    @Override
    public void createOnlineGameInterface(int roomId){
        this.isOnline=true;
        this.roomId=roomId;

        //Affichage du joueur courant
        updateCurrentPlayerName();

        //Création du plateau de jeu
        createBoard();

        //Attendre que la salle se remplisse
        waitPlayer();
    }

    //Crée la grille de jeu dynamiquement
    private void createBoard(int size){
        for(int i=0; i<size; i++){
            ColumnConstraints columnConstraints=new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0/size);
            ticTacToeGridPane.getColumnConstraints().add(columnConstraints);

            RowConstraints rowConstraints=new RowConstraints();
            rowConstraints.setPercentHeight(100.0/size);
            ticTacToeGridPane.getRowConstraints().add(rowConstraints);
        }

        for(int row=0; row<size; row++){
            for(int col=0; col<size; col++){
                //Création des boutons
                Button button=new Button();
                button.setId("button"+row+col);
                button.getStyleClass().add("gameButton");
                button.setOnAction(this::onGridButtonClick);

                //Style des boutons
                button.styleProperty().bind(ticTacToeGridPane.widthProperty().divide(size*2.2).asString("-fx-font-size: %f;"));
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                ticTacToeGridPane.add(button, col, row);
                buttons.add(button);
            }
        }
    }

    private void createBoard(){
        try{
            networkManager.sendMessageToServer(Commands.GET_BOARD_SIZE+" "+roomId);
            int size=Integer.parseInt(networkManager.receiveMessageFromServer());
            if(size>-1) createBoard(size);
        }
        catch(IOException e){
            e.printStackTrace();
            System.out.println("Error while asking board game size.");
        }
    }

    public void addOfflinePlayer(Player player){
        try{
            if(!isOnline){
                game.addPlayer(player);
                player.setGame(game);
            }
        }
        catch(PlayerFullException e){
            System.out.println("Board game is full.");
        }
    }

    //Attendre que les joueurs se réunissent
    private void waitPlayer(){
        networkManager.sendMessageToServer(Commands.IS_ROOM_FULL+" "+roomId);
        try{
            String serverResponse=networkManager.receiveMessageFromServer();
            setPlayerNumber(serverResponse);
        }
        catch(IOException e){
            e.printStackTrace();
            System.out.println("Error while asking room fully state.");
        }
    }

    public void setPlayerNumber(String serverResponse){
        String[] response=serverResponse.split(" ");
        boolean canStart=Boolean.parseBoolean(response[0]);
        if(canStart) roomIdLabel.setText("Room: "+roomId);
        else roomIdLabel.setText("Room: "+roomId+" - "+response[1]+"/"+response[2]+" players");
    }


    /* Boutons */
    private void disableButtons(){
        for(Button button: buttons) button.setDisable(true);
    }

    private void enableButtons(){
        for(Button button: buttons) button.setDisable(false);
    }

    @FXML
    public void onGridButtonClick(ActionEvent actionEvent){
        disableButtons();

        //Extraction du numéro de bouton au format buttonRowCol
        Button clickedButton=(Button)actionEvent.getSource();
        String buttonID=clickedButton.getId();
        int h=Character.getNumericValue(buttonID.charAt(6));
        int w=Character.getNumericValue(buttonID.charAt(7));

        if(isOnline){
            networkManager.sendMessageToServer(Commands.PLAY+" "+currentPlayer.getName()+" "+roomId+" "+h+" "+w);
            try{
                String[] response=networkManager.receiveMessageFromServer().split(" ");
                if(Boolean.parseBoolean(response[0])){
                    Pawn pawn=Pawn.toPawn(response[1]);
                    updateButton(clickedButton, pawn);
                    updateCurrentPlayerName();
                    updateWinner();
                }
                //TODO: Attente de l'action de l'adversaire

            }
            catch(Exception e){
                e.printStackTrace();
                System.out.println("Error while clicking on the board.");
            }
        }
        else{
            try{
                //Ajout du pion joué
                Pawn pawn=game.play(h, w);
                updateButton(clickedButton, pawn);
                updateCurrentPlayerName();
                updateWinner();

                //Si l'adversaire est une ia
                if(game.getCurrentPlayer() instanceof TicTacToeAiPlayer){
                    pawn=game.getCurrentPlayer().play();

                    clickedButton=(Button)rootPane.lookup("#button"+game.getLastHPlayed()+game.getLastWPlayed());
                    updateButton(clickedButton, pawn);
                    game.resetLastPosition();

                    updateCurrentPlayerName();
                    updateWinner();
                }
            }
            catch(InvalidMoveException e){}
        }
    }

    private void updateButton(Button button, Pawn pawn){
        button.setText(pawn.toString());
        button.getStyleClass().add(pawn.toString());
        enableButtons();
    }

    private void updateCurrentPlayerName(){
        if(isOnline){
            try{
                networkManager.sendMessageToServer(Commands.GET_PLAYER_NAME+" "+roomId);
                String currentPlayerName=networkManager.receiveMessageFromServer();
                playerNameLabel.setText("Current player is "+currentPlayerName);
            }
            catch(IOException e){
                e.printStackTrace();
                System.out.println("Error while displaying current player name");
            }
        }
        else{
            Player currentPlayer=game.getCurrentPlayer();
            if(currentPlayer!=null) playerNameLabel.setText("Current player is "+currentPlayer.getName());
        }
    }

    private void updateWinner(){
        if(isOnline){
            try{
                networkManager.sendMessageToServer(Commands.IS_GAME_OVER+" "+roomId);
                String[] response=networkManager.receiveMessageFromServer().split(" ");
                if(Boolean.parseBoolean(response[0])){
                    String winner=response[1];
                    if(winner==null) gameWinnerLabel.setText("Draw");
                    else{
                        gameWinnerLabel.setText("Winner is player "+winner);
                        new HumanPlayer(winner).addScore(10);
                    }
                    showWinner();
                }
            }
            catch(IOException e){
                e.printStackTrace();
                System.out.println("Error while displaying current player name");
            }
        }
        else{
            if(game.isGameOver()){
                Player winner=game.getWinner();
                if(winner==null) gameWinnerLabel.setText("Draw");
                else{
                    gameWinnerLabel.setText("Winner is player "+winner.getName());
                    winner.addScore(10);
                }
                showWinner();
            }
        }
    }

    public void showWinner(){
        //Effet de flou sur le jeu
        allContentVBox.setEffect(new GaussianBlur(10));
        blurOverlay.setVisible(true);
        blurOverlay.setManaged(true);

        //Affiche le gagnant
        gameWinnerAnnounce.setVisible(true);
        gameWinnerAnnounce.setManaged(true);
    }

    @FXML
    public void giveUp(ActionEvent actionEvent){
        try{
            //Création et affichage de la pop-up de demande de confirmation
            Stage newStage=new Stage();
            CustomAlert customAlert=ViewChanger.createAlert("Confirmation", "Give up?", (Stage)rootPane.getScene().getWindow(), newStage);

            //Attente d'une réponse et traitement
            newStage.showAndWait(); //attend que la popup soit fermée
            if(customAlert.getResult()){
                ViewChanger.changeSceneToMenu(actionEvent, currentPlayer, networkManager);
                game.removePlayer(currentPlayer);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Cannot create custom alert confirmation for give up.");
        }
    }

    @FXML
    public void goBack(ActionEvent actionEvent){
        ViewChanger.changeSceneToMenu(actionEvent, currentPlayer, networkManager);
        game.removePlayer(currentPlayer);
    }
}