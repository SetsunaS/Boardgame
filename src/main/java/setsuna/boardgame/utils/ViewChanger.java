package setsuna.boardgame.utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import setsuna.boardgame.GameApplication;
import setsuna.boardgame.controller.IController;
import setsuna.boardgame.utils.network.NetworkManager;
import setsuna.boardgame.model.games.Games;
import setsuna.boardgame.model.general.player.Player;

import java.io.IOException;

public class ViewChanger{
    //Création d'une fenêtre
    private static Scene creatScene(String path) throws IOException{
        FXMLLoader fxmlLoader=new FXMLLoader(GameApplication.class.getResource(path));
        Scene scene=new Scene(fxmlLoader.load());
        return scene;
    }

    public static Scene createLoginScene() throws IOException{
        return creatScene(Constants.LOGIN_VIEW_PATH);
    }


    //Change le contenu de la fenêtre actuelle en mettant le nouveau contenu
    private static FXMLLoader changeScene(ActionEvent actionEvent, String newPath, Player player, NetworkManager networkManager){
        try{
            //Scène à changer
            Scene currentScene=((Node)actionEvent.getSource()).getScene();

            //Scène à mettre
            FXMLLoader loader=new FXMLLoader(GameApplication.class.getResource(newPath));

            //Changement de scène
            currentScene.setRoot(loader.load());

            IController controllerInterface=loader.getController();
            controllerInterface.setCurrentPlayer(player);
            controllerInterface.setNetworkManager(networkManager);

            return loader;
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Error when changing scene to "+newPath);
        }

        return null;
    }

    public static FXMLLoader changeSceneToMenu(ActionEvent actionEvent, Player player, NetworkManager networkManager){
        return changeScene(actionEvent, Constants.MENU_VIEW_PATH, player, networkManager);
    }

    public static FXMLLoader changeSceneToLogin(ActionEvent actionEvent, Player player, NetworkManager networkManager){
        return changeScene(actionEvent, Constants.LOGIN_VIEW_PATH, player, networkManager);
    }

    public static FXMLLoader changeSceneToGame(ActionEvent actionEvent, Games gameNumber, Player player, NetworkManager networkManager){
        return switch(gameNumber){
            case TIC_TAC_TOE -> changeScene(actionEvent, Constants.TIC_TAC_TOE_VIEW_PATH, player, networkManager);
            case TEST -> null;
        };
    }


    //Création et affichage d'une fenêtre d'alerte
    public static CustomAlert createAlert(String titleText, String contentText, Stage parentStage, Stage newStage) throws IOException{
        //Pop-up de demande de confirmation
        FXMLLoader loader=new FXMLLoader(GameApplication.class.getResource(Constants.CUSTOM_ALERT_VIEW_PATH));
        Parent root=loader.load();
        CustomAlert customAlert=loader.getController();

        //Paramétrage de la pop-up
        newStage.setTitle(titleText);
        customAlert.setText(contentText);

        //Bloque l'accès à la fenêtre principale
        newStage.initModality(Modality.APPLICATION_MODAL);

        //Centre la pop-up par rapport à la fenêtre de jeu
        ViewChanger.displayOnCenter(parentStage, newStage);

        //Affichage de la pop-up
        newStage.setScene(new Scene(root));

        return customAlert;
    }


    //Centre la nouvelle fenêtre par rapport à l'ancienne
    private static void displayOnCenter(Stage parentStage, Stage newStage){
        newStage.setOnShown(e -> {
            newStage.setX(parentStage.getX() + (parentStage.getWidth()/2) - (newStage.getWidth()/2));
            newStage.setY(parentStage.getY() + (parentStage.getHeight()/2) - (newStage.getHeight()/2));
        });
    }
}