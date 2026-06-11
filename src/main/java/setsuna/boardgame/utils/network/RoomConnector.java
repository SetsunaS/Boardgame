package setsuna.boardgame.utils.network;

import setsuna.boardgame.model.games.Games;
import java.io.IOException;

/** Connexion au serveur et création/jonction d'une salle, sans dépendance à l'interface graphique **/
public class RoomConnector{
    private final NetworkManager networkManager;
    private final RoomConnectionListener listener;

    public RoomConnector(NetworkManager networkManager, RoomConnectionListener listener){
        this.networkManager=networkManager;
        this.listener=listener;
    }

    public void createRoom(Games selectedGame, int size, String playerName) throws IOException{
        networkManager.connectToServer();
        networkManager.sendMessageToServer(Commands.CREATE_ROOM+" "+selectedGame+" "+size+" "+playerName);

        new Thread(() -> {
            try{
                handleCreateRoomResponse(networkManager.receiveMessageFromServer());
            }
            catch(InterruptedException e){
                System.out.println("Catch interruption.");
            }
        }).start();
    }

    public void joinRoom(int roomId, Games selectedGame, String playerName) throws IOException{
        networkManager.connectToServer();
        networkManager.sendMessageToServer(Commands.JOIN_ROOM+" "+roomId+" "+selectedGame+" "+playerName);

        new Thread(() -> {
            try{
                handleJoinRoomResponse(roomId, networkManager.receiveMessageFromServer());
            }
            catch(InterruptedException e){
                System.out.println("Catch interruption.");
            }
        }).start();
    }

    void handleCreateRoomResponse(String input){
        int roomId=Integer.parseInt(input.split(" ")[1]);
        if(roomId!=-1) listener.onRoomEntered(roomId);
    }

    void handleJoinRoomResponse(int roomId, String input){
        boolean isJoin=Boolean.parseBoolean(input.split(" ")[1]);
        if(isJoin) listener.onRoomEntered(roomId);
        else listener.onRoomJoinFailed();
    }
}
