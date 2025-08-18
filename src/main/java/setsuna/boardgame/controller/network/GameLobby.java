package setsuna.boardgame.controller.network;

import setsuna.boardgame.model.games.GameModel;
import setsuna.boardgame.model.general.exception.PlayerFullException;
import setsuna.boardgame.model.general.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GameLobby{
    private final Map<Integer, GameSession> gameSessions;
    private final AtomicInteger nextRoomNumber;

    public GameLobby(){
        this.gameSessions=new ConcurrentHashMap<>();
        this.nextRoomNumber=new AtomicInteger(777);
    }

    public GameSession getGameSession(int roomId){
        return gameSessions.get(roomId);
    }

    public int createRoom(GameModel gameModel, int maxPlayersNumber, String playerName){
        if(gameModel!=null){
            int roomNumber=nextRoomNumber.getAndIncrement();
            GameSession gameSession=new GameSession(roomNumber, playerName, maxPlayersNumber, gameModel);
            gameSessions.put(roomNumber, gameSession);
            return roomNumber;
        }
        return -1;
    }

    public GameModel joinRoom(int roomNumber, Player player){
        //TODO
        return null;
    }

    public boolean deleteRoom(int roomNumber){
        GameSession session=gameSessions.get(roomNumber);
        if(session.canBeClose()){
            gameSessions.remove(roomNumber);
            return true;
        }

        return false;
    }
}