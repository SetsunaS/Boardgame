package setsuna.boardgame.utils.network;

import setsuna.boardgame.model.general.Pawn;

/** Interprète les messages du serveur : décisions et réponses réseau, sans dépendance à l'interface graphique **/
public class ServerMessageHandler{
    private final NetworkManager networkManager;
    private final GameClientListener listener;
    private final String playerName;
    private final int roomId;

    public ServerMessageHandler(NetworkManager networkManager, GameClientListener listener, String playerName, int roomId){
        this.networkManager=networkManager;
        this.listener=listener;
        this.playerName=playerName;
        this.roomId=roomId;
    }

    public void handleServerMessage(String input){
        System.out.println("received message from server : "+input);

        String[] message=input.split(" ");
        switch(Commands.valueOf(message[0])){
            case CREATE_ROOM: {
                //Déjà géré dans MenuController
                break;
            }

            case JOIN_ROOM: {
                //Déjà géré dans MenuController
                break;
            }

            case IS_ROOM_FULL: {
                boolean isRoomFull=Boolean.parseBoolean(message[1]);
                int currentPlayerNumber=Integer.parseInt(message[2]);
                int maxPlayersNumber=Integer.parseInt(message[3]);

                if(isRoomFull){
                    listener.onRoomFull();
                    networkManager.sendMessageToServer(Commands.GET_PLAYER_NAME+" "+roomId);
                }
                else listener.onRoomWaiting(currentPlayerNumber, maxPlayersNumber);
                break;
            }

            case GET_PLAYER_NAME: {
                String currentPlayerName=message[1];
                listener.onCurrentPlayer(currentPlayerName, currentPlayerName.equals(playerName));
                break;
            }

            case GET_BOARD_SIZE: {
                int boardSize=Integer.parseInt(message[1]);
                if(boardSize>0) listener.onBoardCreation(boardSize);
                break;
            }

            case PLAY: {
                boolean isValidMove=Boolean.parseBoolean(message[1]);
                if(isValidMove){
                    Pawn pawn=Pawn.toPawn(message[2]);
                    int h=Integer.parseInt(message[3]);
                    int w=Integer.parseInt(message[4]);
                    int boardSize=Integer.parseInt(message[5]);

                    listener.onMovePlayed(pawn, h, w, boardSize);

                    //Demande si la partie est finie
                    networkManager.sendMessageToServer(Commands.IS_GAME_OVER+" "+roomId+" "+playerName);
                }
                break;
            }

            case IS_GAME_OVER: {
                boolean isGameOver=Boolean.parseBoolean(message[1]);
                if(isGameOver){
                    String winner=message[2];
                    if(winner.equals("null")) listener.onGameOver(null, false);
                    else listener.onGameOver(winner, winner.equals(playerName));

                    networkManager.closeConnection();
                }
                else networkManager.sendMessageToServer(Commands.GET_PLAYER_NAME+" "+roomId);
                break;
            }

            case GIVE_UP: {
                //Chaque client demande la fin de partie avec son propre nom pour être retiré de la salle
                networkManager.sendMessageToServer(Commands.IS_GAME_OVER+" "+roomId+" "+playerName);
                break;
            }

            default:
        }
    }
}
