package setsuna.boardgame;

import javafx.application.Application;
import javafx.stage.Stage;
import setsuna.boardgame.controller.network.GameServer;
import setsuna.boardgame.utils.ViewChanger;

import java.io.IOException;

public class GameApplication extends Application{
    @Override
    public void start(Stage stage) throws IOException{
        stage.setMaximized(true);
        stage.setTitle("Boardgame");

        stage.setScene(ViewChanger.createLoginScene());
        stage.show();

        //Crée le serveur et le lance dans un thread séparé pour ne pas bloquer l'application
        GameServer server=new GameServer();
        new Thread(() -> {
            try{
                server.start();
            }
            catch(IOException e){}
        }).start();
    }

    public static void main(String[] args){
        launch();
    }
}