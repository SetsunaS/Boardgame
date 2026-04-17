package setsuna.boardgame.server;

import setsuna.boardgame.model.games.GameModel;
import setsuna.boardgame.model.games.Games;
import setsuna.boardgame.model.general.Pawn;
import setsuna.boardgame.model.general.exception.InvalidMoveException;
import setsuna.boardgame.model.general.exception.PlayerFullException;
import java.io.PrintWriter;
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

    public int createRoom(GameModel gameModel, String hostName, PrintWriter writer) throws PlayerFullException{
        if(gameModel!=null){
            int roomId=nextRoomNumber.getAndIncrement();
            GameSession session=new GameSession(gameModel, hostName, writer);
            gameSessions.put(roomId, session);
            return roomId;
        }
        return -1;
    }

    public boolean joinRoom(int roomId, Games selectedGame, String player, PrintWriter writer) throws PlayerFullException{
        GameSession session=gameSessions.get(roomId);
        if(session!=null){
            return session.joinRoom(selectedGame, player, writer);
        }
        return false;
    }

    public boolean giveUp(int roomId, String playerName){
        GameSession session=getGameSession(roomId);
        if(session!=null){
            return session.giveUp(playerName);
        }
        return false;
    }

    public String isRoomFull(int roomId){
        GameSession session=getGameSession(roomId);
        if(session!=null){
            int currentPlayerNumber=session.getCurrentPlayerNumber();
            int maxPlayersNumber=session.getMaxPlayersNumber();
            return (currentPlayerNumber==maxPlayersNumber)+" "+currentPlayerNumber+" "+maxPlayersNumber;
        }
        return null;
    }

    public String play(String playerName, int roomId, int h, int w) throws InvalidMoveException{
        GameSession session=getGameSession(roomId);
        if(session!=null && playerName.equals(session.getCurrentPlayerName()) && session.isValidMove(h, w)){
            Pawn pawn=session.play(h, w);
            int boardSize=session.getBoardSize();
            return true+" "+pawn+" "+h+" "+w+" "+boardSize;
        }
        return false+"";
    }

    public void broadcast(int roomId, String message){
        GameSession session=getGameSession(roomId);
        if(session!=null) session.broadcast(message);
    }

    public String getPlayerName(int roomId){
        GameSession session=getGameSession(roomId);
        if(session!=null) return session.getCurrentPlayerName();
        return null;
    }

    public int getBoardSize(int roomId){
        GameSession session=getGameSession(roomId);
        if(session!=null) return session.getBoardSize();
        return -1;
    }

    public String isGameOver(int roomId, String playerName){
        GameSession session=gameSessions.get(roomId);
        if(session!=null){
            boolean isGameOver=session.isGameOver();

            String winner=null;
            if(isGameOver){
                winner=session.getWinner();

                //Supression de la session
                session.removePlayer(playerName);
                deleteRoom(roomId);
            }

            return isGameOver+" "+winner;
        }
        return null;
    }

    public boolean deleteRoom(int roomId){
        GameSession session=gameSessions.get(roomId);
        if(session!=null && session.canBeClose()){
            gameSessions.remove(roomId);
            return true;
        }

        return false;
    }
}