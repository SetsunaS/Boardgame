package setsuna.boardgame;

import javafx.application.Application;
import javafx.stage.Stage;
import setsuna.boardgame.utils.ViewChanger;

import java.io.IOException;

public class GameApplication extends Application{
    @Override
    public void start(Stage stage) throws IOException{
        stage.setMaximized(true);
        stage.setTitle("Boardgame");

        stage.setScene(ViewChanger.createLoginScene());
        stage.show();
    }

    public static void main(String[] args){
        launch();
    }
}