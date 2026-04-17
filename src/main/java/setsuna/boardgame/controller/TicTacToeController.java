package setsuna.boardgame.controller;

import javafx.application.Platform;
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
import java.util.ArrayList;
import java.util.List;

public class TicTacToeController implements IController, IGameController{
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

    /** Gestion de fin de partie **/
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
                if(isOnline) networkManager.sendMessageToServer(Commands.GIVE_UP+" "+roomId+" "+currentPlayer.getName());
                else game.removePlayer(currentPlayer);
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
        if(!isOnline) game.removePlayer(currentPlayer);
    }


    /******************************/
    /** Jeu de base - hors ligne **/
    /******************************/

    /** Création de l'interface de jeu **/
    @Override
    public void createOfflineGameInterface(List<Player> players, int size){
        this.isOnline=false;

        //Gestion du model dans le controller si jeu en local
        this.game=new TicTacToe(size);

        //Joueurs
        for(Player player: players){
            try{
                game.addPlayer(player);
                player.setGame(game);
            }
            catch(PlayerFullException e){
                System.out.println("Board game is full.");
            }
        }

        updateCurrentPlayerName();

        //Création du plateau de jeu
        createBoard(size);
    }


    /******************/
    /** Jeu en ligne **/
    /******************/

    @Override
    public void createOnlineGameInterface(int roomId){
        this.isOnline=true;
        this.roomId=roomId;

        //Communication avec le serveur
        Thread update=new Thread(() -> {
            String message;
            try{
                while(networkManager.getIsRunning()){
                    message=networkManager.receiveMessageFromServer();
                    if(message!=null) handleServerMessage(message);
                }
            }
            catch(InterruptedException e){
                System.out.println("Error with interruption.");
            }
        });
        update.start();

        //Création du plateau de jeu
        networkManager.sendMessageToServer(Commands.GET_BOARD_SIZE+" "+roomId);
    }

    private void handleServerMessage(String input){
        System.out.println("received message from server : "+input);

        String[] message=input.split(" ");
        switch(Commands.valueOf(message[0])){
            case CREATE_ROOM: {
                //Déjà géré dans MenuController
                break;
            }

            case JOIN_ROOM: {
                //Déjà géré dans MenuController
                break;
            }

            case IS_ROOM_FULL: {
                //Affichage du nombre de joueurs dans la salle
                boolean isRoomFull=Boolean.parseBoolean(message[1]);
                int currentPlayerNumber=Integer.parseInt(message[2]);
                int maxPlayersNumber=Integer.parseInt(message[3]);

                if(isRoomFull){
                    Platform.runLater(() -> {
                        roomIdLabel.setText("Room: "+roomId);
                        networkManager.sendMessageToServer(Commands.GET_PLAYER_NAME+" "+roomId);
                    });
                }
                else
                    Platform.runLater(() -> roomIdLabel.setText("Room: "+roomId+" - "+currentPlayerNumber+"/"+maxPlayersNumber+" players"));
                break;
            }

            case GET_PLAYER_NAME: {
                String currentPlayerName=message[1];
                Platform.runLater(() -> playerNameLabel.setText("Current player is "+currentPlayerName));
                if(currentPlayerName.equals(currentPlayer.getName()))
                    Platform.runLater(this::enableButtons);
                break;
            }

            case GET_BOARD_SIZE: {
                int boardSize=Integer.parseInt(message[1]);
                if(boardSize>0){
                    Platform.runLater(() -> {
                        createBoard(boardSize);
                        disableButtons();
                    });
                }
                break;
            }

            case PLAY: {
                boolean isValidMove=Boolean.parseBoolean(message[1]);
                if(isValidMove){
                    Pawn pawn=Pawn.toPawn(message[2]);
                    int h=Integer.parseInt(message[3]);
                    int w=Integer.parseInt(message[4]);
                    int boardSize=Integer.parseInt(message[5]);

                    Platform.runLater(() -> {
                        disableButtons();
                        updateButton(getButton(h, w, boardSize), pawn);
                    });

                    //Update winner
                    networkManager.sendMessageToServer(Commands.IS_GAME_OVER+" "+roomId+" "+currentPlayer.getName());
                }
                break;
            }

            case IS_GAME_OVER: {
                boolean isGameOver=Boolean.parseBoolean(message[1]);
                if(isGameOver){
                    String winner=message[2];
                    if(winner.equals("null")) Platform.runLater(() -> gameWinnerLabel.setText("Draw"));
                    else{
                        Platform.runLater(() -> gameWinnerLabel.setText("Winner is player "+winner));
                        new HumanPlayer(winner).addScore(10);
                    }
                    Platform.runLater(this::showWinner);
                    networkManager.closeConnection();
                }
                else networkManager.sendMessageToServer(Commands.GET_PLAYER_NAME+" "+roomId);
                break;
            }

            case GIVE_UP: {
                String playerName=message[1];
                networkManager.sendMessageToServer(Commands.IS_GAME_OVER+" "+roomId+" "+playerName);
                break;
            }

            default:
        }
    }


    /*********************/
    /** Modification UI **/
    /*********************/

    /** Gestion des joueurs **/
    private void updateCurrentPlayerName(){
        Player currentPlayer=game.getCurrentPlayer();
        if(currentPlayer!=null) playerNameLabel.setText("Current player is "+currentPlayer.getName());
    }

    private void updateWinner(){
        if(game.isGameOver()){
            Player winner=game.getWinner();
            if(winner==null) gameWinnerLabel.setText("Draw");
            else{
                gameWinnerLabel.setText("Winner is player "+winner.getName());
                winner.addScore(10);
            }
            showWinner();
        }
        else updateCurrentPlayerName();
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

    /** Gestion de la grille **/
    //Création de la grille de jeu dynamiquement
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

    /** Gestion des boutons **/
    private void disableButtons(){
        for(Button button: buttons) button.setDisable(true);
    }

    private void enableButtons(){
        for(Button button: buttons) button.setDisable(false);
    }

    private void updateButton(Button button, Pawn pawn){
        button.setText(pawn.toString());
        button.getStyleClass().add(pawn.toString());
    }

    private Button getButton(int h, int w, int boardSize){
        int index=h*boardSize+w;
        return buttons.get(index);
    }

    @FXML
    public void onGridButtonClick(ActionEvent actionEvent){
        //Extraction du numéro de bouton au format buttonRowCol
        Button clickedButton=(Button)actionEvent.getSource();
        String buttonID=clickedButton.getId();
        int h=Character.getNumericValue(buttonID.charAt(6));
        int w=Character.getNumericValue(buttonID.charAt(7));

        if(isOnline) networkManager.sendMessageToServer(Commands.PLAY+" "+currentPlayer.getName()+" "+roomId+" "+h+" "+w);
        else{
            try{
                //Ajout du pion joué
                Pawn pawn=game.play(h, w);
                updateButton(clickedButton, pawn);
                updateWinner();

                //Si l'adversaire est une ia
                if(game.getCurrentPlayer() instanceof TicTacToeAiPlayer){
                    pawn=game.getCurrentPlayer().play();

                    clickedButton=(Button)rootPane.lookup("#button"+game.getLastHPlayed()+game.getLastWPlayed());
                    updateButton(clickedButton, pawn);
                    game.resetLastPosition();

                    updateWinner();
                }
            }
            catch(InvalidMoveException e){}
        }
    }
}