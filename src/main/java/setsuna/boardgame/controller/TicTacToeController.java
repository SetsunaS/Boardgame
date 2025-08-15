package setsuna.boardgame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import setsuna.boardgame.model.games.TicTacToe;
import setsuna.boardgame.model.general.Pawn;
import setsuna.boardgame.model.general.Player;
import setsuna.boardgame.model.general.exception.InvalidMoveException;
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

        //Joueur courant
        changeCurrentPlayerName();

        //Plateau de jeu
        createBoard(size);
    }

    private void changeCurrentPlayerName(){
        //TODO: changer quand il y aura des joueurs
        //playerNameLabel.setText("Current player is "+game.getCurrentPlayer().getName());
        playerNameLabel.setText("Current player is "+game.getTestName());
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
        Button clickedButton=(Button)actionEvent.getSource();

        //Extraction du numéro de bouton au format buttonRowCol
        String buttonID=clickedButton.getId();
        int h=Character.getNumericValue(buttonID.charAt(6));
        int w=Character.getNumericValue(buttonID.charAt(7));

        try{
            //Ajout du pion joué
            Pawn pawn=game.play(h, w);
            clickedButton.setText(pawn.toString());
            clickedButton.getStyleClass().add(pawn.toString());

            //Joueur courant
            changeCurrentPlayerName();

            //Fin de jeu
            if(game.isGameOver()){
                Player winner=game.getWinner();
                showWinner();
                if(winner==null) gameWinnerLabel.setText("Draw");
                else gameWinnerLabel.setText("Winner is player "+winner.getName());
            }
        }
        catch(InvalidMoveException e){}
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