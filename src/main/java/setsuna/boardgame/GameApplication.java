package setsuna.boardgame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GameApplication extends Application{
    @Override
    public void start(Stage stage) throws IOException{
        stage.setMaximized(true);
        stage.setTitle("Boardgame");

        FXMLLoader fxmlLoader=new FXMLLoader(GameApplication.class.getResource("MenuView/MenuView.fxml"));
        Scene scene=new Scene(fxmlLoader.load());

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        launch();
    }
}