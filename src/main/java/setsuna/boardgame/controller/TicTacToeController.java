package setsuna.boardgame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import setsuna.boardgame.GameApplication;
import setsuna.boardgame.model.games.TicTacToe;
import setsuna.boardgame.model.general.Pawn;
import setsuna.boardgame.model.general.Player;
import setsuna.boardgame.model.general.exception.InvalidMoveException;
import setsuna.boardgame.utils.CustomAlert;

import java.io.IOException;

public class TicTacToeController{
    @FXML
    private StackPane rootPane;

    @FXML
    private VBox allContentVBox;

    @FXML
    private HBox annexeVBox;

    @FXML
    private Label playerNameLabel;

    @FXML
    private Button giveUpButton;

    @FXML
    private GridPane ticTacToeGridPane;

    @FXML
    private Region blurOverlay;

    @FXML
    private VBox gameWinnerAnnounce;

    @FXML
    private Label gameWinnerLabel;

    private TicTacToe game;

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
            //Fenêtre de demande de confirmation
            FXMLLoader loader=new FXMLLoader(GameApplication.class.getResource("utils/CustomAlertView.fxml"));
            Parent root=loader.load();

            CustomAlert controller=loader.getController();
            controller.setText("Give up?");

            Stage stage=new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); //bloque l'accès à la fenêtre principale
            stage.setTitle("Confirmation");
            stage.setScene(new Scene(root));
            stage.showAndWait(); //attend que la popup soit fermée

            if(controller.getResult()) changeSceneToMenu(actionEvent);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    public void goBack(ActionEvent actionEvent){
        changeSceneToMenu(actionEvent);
    }

    private void changeSceneToMenu(ActionEvent actionEvent){
        try{
            //Scène à changer
            Scene currentScene=((Node)actionEvent.getSource()).getScene();

            //Scène à mettre
            Parent newRoot=new FXMLLoader(GameApplication.class.getResource("MenuView/MenuView.fxml")).load();

            //Changement de scène
            currentScene.setRoot(newRoot);
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }
}