package setsuna.boardgame.server;

import setsuna.boardgame.model.games.GameModel;
import setsuna.boardgame.model.games.Games;
import setsuna.boardgame.model.games.TicTacToe;
import setsuna.boardgame.model.general.exception.InvalidMoveException;
import setsuna.boardgame.model.general.exception.PlayerFullException;
import setsuna.boardgame.utils.Constants;
import setsuna.boardgame.utils.network.Commands;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer{
    private ServerSocket serverSocket;
    private final GameLobby gameLobby;

    public GameServer(){
        this.gameLobby=new GameLobby();
    }

    public void start(){
        //Initialisation du port
        try{
            serverSocket=new ServerSocket(Constants.SERVER_PORT);
        }
        catch(IOException e){
            e.printStackTrace();
            System.out.println("Error while creating new ServerSocket with port "+Constants.SERVER_PORT);
        }

        ExecutorService threadPool=Executors.newFixedThreadPool(10);
        while(true){
            try{
                //Connexion des clients
                Socket clientSocket=serverSocket.accept();

                //Gestion de la communication avec les clients
                threadPool.submit(() -> handleClient(clientSocket));
            }
            catch(IOException e){
                e.printStackTrace();
                System.out.println("Error while accepting client socket.");
            }
        }
    }

    private void handleClient(Socket clientSocket){
        try(BufferedReader in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out=new PrintWriter(clientSocket.getOutputStream(), true)){

            String inputLine;
            String[] message;
            while((inputLine=in.readLine())!=null){
                System.out.println(inputLine);

                message=inputLine.split(" ");
                switch(Commands.valueOf(message[0])){
                    case CREATE_ROOM: {
                        Games game=Games.valueOf(message[1]);
                        int gameSize=Integer.parseInt(message[2]);
                        String hostName=message[3];

                        int roomId=gameLobby.createRoom(getGameModel(game, gameSize), hostName, out);
                        out.println(roomId);

                        //Réponse à IS_ROOM_FULL
                        gameLobby.broadcast(roomId, gameLobby.isRoomFull(roomId));
                        break;
                    }

                    case JOIN_ROOM: {
                        int roomId=Integer.parseInt(message[1]);
                        Games selectedGame=Games.valueOf(message[2]);
                        String playerName=message[3];

                        boolean isJoin=gameLobby.joinRoom(roomId, selectedGame, playerName, out);
                        out.println(isJoin);

                        //Réponse à IS_ROOM_FULL
                        if(isJoin) gameLobby.broadcast(roomId, gameLobby.isRoomFull(roomId));
                        break;
                    }

                    case IS_ROOM_FULL: {
                        //Appelé que lors de l'attente de joueurs, donc réponse danns JOIN_ROOM
                        break;
                    }

                    case GET_PLAYER_NAME: {
                        int roomId=Integer.parseInt(message[1]);

                        String currentPlayerName=gameLobby.getPlayerName(roomId);
                        out.println(currentPlayerName);
                        break;
                    }

                    case GET_BOARD_SIZE: {
                        int roomId=Integer.parseInt(message[1]);

                        int size=gameLobby.getBoardSize(roomId);
                        out.println(size);
                        break;
                    }

                    case PLAY: {
                        String playerName=message[1];
                        int roomId=Integer.parseInt(message[2]);
                        int h=Integer.parseInt(message[3]);
                        int w=Integer.parseInt(message[4]);

                        String playRes=gameLobby.play(playerName, roomId, h, w);
                        if(playRes.equals("false")) out.println(playRes);
                        else gameLobby.broadcast(roomId, playRes);
                        break;
                    }

                    case IS_GAME_OVER: {
                        int roomId=Integer.parseInt(message[1]);
                        String playerName=message[2];

                        String isGameOverRes=gameLobby.isGameOver(roomId, playerName);
                        out.println(isGameOverRes);
                        break;
                    }

                    case GIVE_UP: {
                        int roomId=Integer.parseInt(message[1]);
                        String playerName=message[2];

                        boolean giveUpRes=gameLobby.giveUp(roomId, playerName);
                        out.println(giveUpRes);
                        break;
                    }

                    default:
                }
            }
        }
        catch(IOException e){
            System.out.println("Communication error with clients.");
        }
        catch(PlayerFullException|InvalidMoveException e){
            throw new RuntimeException(e);
        }
        finally{
            try{
                clientSocket.close();
            }
            catch(IOException e){
                System.out.println("Error while closing client socket.");
            }
        }
    }

    private GameModel getGameModel(Games game, int size){
        switch(game){
            case TIC_TAC_TOE: return new TicTacToe(size);
            default: return null;
        }
    }

    public void stop() throws IOException{
        serverSocket.close();
    }

    public static void main(String[] args){
        //Crée le serveur et le lance
        GameServer server=new GameServer();
        server.start();
    }
}