package setsuna.boardgame.controller.network;

import setsuna.boardgame.model.games.GameModel;
import setsuna.boardgame.model.games.TicTacToe;
import setsuna.boardgame.utils.Constants;

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

    public void start() throws IOException{
        //Initialisation du port
        serverSocket=new ServerSocket(Constants.SERVER_PORT);

        ExecutorService threadPool=Executors.newFixedThreadPool(10);
        while(true){
            //Connexion des clients
            Socket clientSocket=serverSocket.accept();

            //Gestion de la communication avec les clients
            threadPool.submit(() -> handleClient(clientSocket));
        }
    }

    private void handleClient(Socket clientSocket){
        try(BufferedReader in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out=new PrintWriter(clientSocket.getOutputStream(), true)){

            String inputLine;
            String[] message;
            while((inputLine=in.readLine())!=null){
                message=inputLine.split(" ");
                switch(message[0]){
                    //Client : CREATE_ROOM (String)gameModel (int)gameSize (int)maxPlayersNumber (String)hostName
                    //Serveur : (int)roomID
                    case "CREATE_ROOM": {
                        System.out.println(inputLine);

                        //Création de la chambre
                        String gameModel=message[1];
                        int gameSize=Integer.parseInt(message[2]);
                        int maxPlayersNumber=Integer.parseInt(message[3]);
                        String hostName=message[4];
                        int roomId=gameLobby.createRoom(getGameModel(gameModel, gameSize), maxPlayersNumber, hostName);

                        //Envoie de la réponse au client
                        out.println(roomId);

                        //Ajout du client à la liste des récepteurs
                        GameSession gameSession=gameLobby.getGameSession(roomId);
                        gameSession.addClient(hostName, out);
                        break;
                    }

                    case "JOIN_ROOM": break;

                    case "PLAY": break;

                    //Client : GET_PLAYER_NAME (int)roomId
                    case "GET_PLAYER_NAME": break;
                    default:
                }
            }
        }
        catch(IOException e){
            System.out.println("Communication error with clients.");
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

    private GameModel getGameModel(String gameModel, int size){
        switch(gameModel){
            case "TIC_TAC_TOE": return new TicTacToe(size);
            default: return null;
        }
    }

    public void stop() throws IOException{
        serverSocket.close();
    }
}