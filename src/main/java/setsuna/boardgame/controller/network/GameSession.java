package setsuna.boardgame.controller.network;

import setsuna.boardgame.model.games.GameModel;
import setsuna.boardgame.model.general.exception.PlayerFullException;
import setsuna.boardgame.model.general.player.Player;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameSession{
    private final int roomId;
    private final List<String> playersName;
    private int currentPlayerIndex=0;
    private final int maxPlayersNumber;
    private final GameModel gameModel;
    private final Map<String, PrintWriter> clientsWriter;

    public GameSession(int roomId, String host, int maxPlayersNumber, GameModel gameModel){
        this.roomId=roomId;

        this.playersName=new ArrayList<>();
        playersName.add(host);
        this.maxPlayersNumber=maxPlayersNumber;

        this.gameModel=gameModel;

        this.clientsWriter=new ConcurrentHashMap<>();
    }

    public int getRoomId(){
        return roomId;
    }

    public GameModel getGameModel(){
        return gameModel;
    }

    public String getCurrentPlayerName(){
        return playersName.get(currentPlayerIndex);
    }

    public void addClient(String playerName, PrintWriter out){
        clientsWriter.put(playerName, out);
    }

    public void addPlayerName(String playerName) throws PlayerFullException{
        if(playersName.size()<maxPlayersNumber){
            playersName.add(playerName);
            //TODO: prévenir les autres joueurs de l'ajout du joueur courant
            broadcastMessage("");
        }
        else throw new PlayerFullException();
    }

    public boolean isFull(){
        return playersName.size()==maxPlayersNumber;
    }

    public boolean canBeClose(){
        return gameModel.canBeClose();
    }

    public void broadcastMessage(String message){
        //TODO
    }
}