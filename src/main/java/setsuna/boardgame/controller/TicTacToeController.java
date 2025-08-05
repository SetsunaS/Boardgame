package setsuna.boardgame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import setsuna.boardgame.model.games.TicTacToe;
import setsuna.boardgame.model.general.Pawn;
import setsuna.boardgame.model.general.Player;
import setsuna.boardgame.model.general.exception.InvalidMoveException;

public class TicTacToeController{
    @FXML
    private StackPane rootPane;

    @FXML
    private GridPane ticTacToeGridPane;

    @FXML
    private Button button00;

    @FXML
    private Button button01;

    @FXML
    private Button button02;

    @FXML
    private Button button10;

    @FXML
    private Button button11;

    @FXML
    private Button button12;

    @FXML
    private Button button20;

    @FXML
    private Button button21;

    @FXML
    private Button button22;

    @FXML
    private Region blurOverlay;

    @FXML
    private VBox gameWinnerAnnounce;

    @FXML
    private Label gameWinner;

    private final TicTacToe game=new TicTacToe();

    @FXML
    private void initialize(){
        //Fixe la taille de la grille de jeu selon la taille la plus petite de largeur/longueur
        ticTacToeGridPane.maxWidthProperty().bind(rootPane.heightProperty().multiply(0.75));
        ticTacToeGridPane.maxHeightProperty().bind(rootPane.heightProperty().multiply(0.75));

        //Fixe les marges
        ticTacToeGridPane.hgapProperty().bind(ticTacToeGridPane.heightProperty().multiply(0.01));
        ticTacToeGridPane.vgapProperty().bind(ticTacToeGridPane.heightProperty().multiply(0.01));

        //Fixe la taille de la police des boutons
        bindFontSize(button00);
        bindFontSize(button01);
        bindFontSize(button02);
        bindFontSize(button10);
        bindFontSize(button11);
        bindFontSize(button12);
        bindFontSize(button20);
        bindFontSize(button21);
        bindFontSize(button22);
    }

    private void bindFontSize(Button button){
        button.styleProperty().bind(button.widthProperty().divide(2.2).asString("-fx-font-size: %f;"));
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

            //Fin de jeu
            if(game.isGameOver()){
                Player winner=game.getWinner();
                showWinner();
                if(winner==null) gameWinner.setText("Draw");
                else gameWinner.setText("Winner is player "+winner.getName());
            }
        }
        catch(InvalidMoveException e){}
    }

    public void showWinner(){
        //Effet de flou sur le jeu
        ticTacToeGridPane.setEffect(new GaussianBlur(10));
        blurOverlay.setVisible(true);
        blurOverlay.setManaged(true);

        //Affiche le gagnant
        gameWinnerAnnounce.setVisible(true);
        gameWinnerAnnounce.setManaged(true);
    }
}