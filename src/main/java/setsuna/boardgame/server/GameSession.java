package setsuna.boardgame.server;

import setsuna.boardgame.model.games.GameModel;
import setsuna.boardgame.model.general.Pawn;
import setsuna.boardgame.model.general.exception.InvalidMoveException;
import setsuna.boardgame.model.general.exception.PlayerFullException;
import setsuna.boardgame.model.general.player.HumanPlayer;
import setsuna.boardgame.model.general.player.Player;

import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameSession{
    private final GameModel gameModel;
    private final Map<String, PrintWriter> clientsWriter;

    public GameSession(GameModel gameModel, String host, PrintWriter out) throws PlayerFullException{
        this.gameModel=gameModel;

        gameModel.addPlayer(new HumanPlayer(host));
        this.clientsWriter=new ConcurrentHashMap<>();
        clientsWriter.put(host, out);
    }

    public boolean canAddPlayer(){
        return gameModel.canAddPlayer();
    }

    public int getMaxPlayersNumber(){
        return gameModel.getMaxPlayersNumber();
    }

    public int getCurrentPlayerNumber(){
        return gameModel.getCurrentPlayerNumber();
    }

    public boolean joinRoom(String playerName, PrintWriter out) throws PlayerFullException{
        if(canAddPlayer()){
            gameModel.addPlayer(new HumanPlayer(playerName));
            clientsWriter.put(playerName, out);
            return true;
        }
        return false;
    }

    public void broadcast(String message){
        for(PrintWriter writer: clientsWriter.values())
            writer.println(message);
    }

    public boolean removePlayer(String playerName){
        return gameModel.removePlayer(new HumanPlayer(playerName));
    }

    public boolean canBeClose(){
        return gameModel.canBeClose();
    }

    public String getCurrentPlayerName(){
        return gameModel.getCurrentPlayer().getName();
    }


    public int getBoardSize(){
        return gameModel.getBoardSize();
    }

    public boolean isValidMove(int h, int w){
        return gameModel.isValidMove(h, w);
    }


    public Pawn play(int h, int w) throws InvalidMoveException{
        return gameModel.play(h, w);
    }

    public boolean isGameOver(){
        return gameModel.isGameOver();
    }

    public String getWinner(){
        Player winner=gameModel.getWinner();
        if(winner==null) return null;
        return winner.getName();
    }

    public boolean giveUp(String playerName){
        return gameModel.giveUp(playerName);
    }
}