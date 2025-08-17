package setsuna.boardgame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import setsuna.boardgame.model.general.player.Player;
import setsuna.boardgame.model.general.player.ai.TicTacToeAiPlayer;
import setsuna.boardgame.model.games.TicTacToe;
import setsuna.boardgame.model.general.Pawn;
import setsuna.boardgame.model.general.exception.InvalidMoveException;
import setsuna.boardgame.model.general.exception.PlayerFullException;
import setsuna.boardgame.utils.CustomAlert;
import setsuna.boardgame.utils.ViewChanger;

public class TicTacToeController implements GameController{
    @FXML
    private StackPane rootPane;

    @FXML
    private VBox allContentVBox;

    @FXML
    private HBox annexeVBox;

    @FXML
    private Label playerNameLabel;

    @FXML
    private GridPane ticTacToeGridPane;

    @FXML
    private Region blurOverlay;

    @FXML
    private VBox gameWinnerAnnounce;

    @FXML
    private Label gameWinnerLabel;

    private TicTacToe game;

    private Player currentPlayer;

    @Override
    public void setCurrentPlayer(Player player){
        currentPlayer=player;
    }

    public TicTacToe getGame(){
        return game;
    }

    @FXML
    private void initialize(){
        //Fixe la taille de la grille de jeu selon la taille la plus petite de largeur/longueur
        ticTacToeGridPane.maxWidthProperty().bind(rootPane.heightProperty().multiply(0.75));
        ticTacToeGridPane.maxHeightProperty().bind(rootPane.heightProperty().multiply(0.75));

        //Même longueur pour la ligne annexe d'information/bouton de retour
        annexeVBox.maxWidthProperty().bind(rootPane.heightProperty().multiply(0.75));

        //Fixe les marges entre les cases
        ticTacToeGridPane.hgapProperty().bind(ticTacToeGridPane.heightProperty().multiply(0.01));
        ticTacToeGridPane.vgapProperty().bind(ticTacToeGridPane.heightProperty().multiply(0.01));
    }

    //Créer la grille de jeu dynamiquement
    public void createGameInterface(int size){
        game=new TicTacToe(size);
        addPlayer(currentPlayer);

        //Joueur courant
        changeCurrentPlayerName();

        //Plateau de jeu
        createBoard(size);
    }

    public void addPlayer(Player player){
        try{
            game.addPlayer(player);
        }
        catch(PlayerFullException e){
            System.out.println("Board game is full.");
        }
    }

    private void changeCurrentPlayerName(){
        if(game.getCurrentPlayer()!=null) playerNameLabel.setText("Current player is "+game.getCurrentPlayer().getName());
    }

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
            }
        }
    }

    @FXML
    public void onGridButtonClick(ActionEvent actionEvent){
        //Extraction du numéro de bouton au format buttonRowCol
        Button clickedButton=(Button)actionEvent.getSource();
        String buttonID=clickedButton.getId();
        int h=Character.getNumericValue(buttonID.charAt(6));
        int w=Character.getNumericValue(buttonID.charAt(7));

        try{
            //Ajout du pion joué
            Pawn pawn=game.play(h, w);
            updateButton(clickedButton, pawn);
            updatePlayer();

            //Si l'adversaire est une ia
            if(game.getCurrentPlayer() instanceof TicTacToeAiPlayer){
                pawn=game.getCurrentPlayer().play();

                clickedButton=(Button)rootPane.lookup("#button"+game.getLastHPlayed()+game.getLastWPlayed());
                updateButton(clickedButton, pawn);
                game.resetLastPosition();

                updatePlayer();
            }
        }
        catch(InvalidMoveException e){}
    }

    private void updateButton(Button button, Pawn pawn){
        button.setText(pawn.toString());
        button.getStyleClass().add(pawn.toString());
    }

    private void updatePlayer(){
        //Joueur courant
        changeCurrentPlayerName();

        //Fin de jeu
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
            if(customAlert.getResult()) ViewChanger.changeSceneToMenu(actionEvent, currentPlayer);
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Cannot create custom alert confirmation for give up.");
        }
    }

    @FXML
    public void goBack(ActionEvent actionEvent){
        ViewChanger.changeSceneToMenu(actionEvent, currentPlayer);
    }
}