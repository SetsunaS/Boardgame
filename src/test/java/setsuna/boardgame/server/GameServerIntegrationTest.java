package setsuna.boardgame.server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import setsuna.boardgame.utils.network.Commands;
import setsuna.boardgame.utils.network.NetworkManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class GameServerIntegrationTest{
    private static GameServer server;
    private static int port;

    private NetworkManager host;
    private NetworkManager guest;

    @BeforeAll
    static void startServer() throws Exception{
        //Port libre aléatoire pour ne pas entrer en conflit avec un serveur déjà lancé sur 7777
        try(ServerSocket probe=new ServerSocket(0)){
            port=probe.getLocalPort();
        }

        server=new GameServer(port);
        Thread serverThread=new Thread(server::start);
        serverThread.setDaemon(true);
        serverThread.start();

        //Attend que le serveur accepte les connexions
        IOException lastError=null;
        for(int i=0; i<50; i++){
            try(Socket socket=new Socket("127.0.0.1", port)){
                return;
            }
            catch(IOException e){
                lastError=e;
                Thread.sleep(100);
            }
        }
        fail("Server did not start: "+lastError);
    }

    @AfterAll
    static void stopServer() throws IOException{
        server.stop();
    }

    @BeforeEach
    void connectClients() throws IOException{
        host=new NetworkManager();
        host.connectToServer("127.0.0.1", port);

        guest=new NetworkManager();
        guest.connectToServer("127.0.0.1", port);
    }

    @AfterEach
    void disconnectClients(){
        host.closeConnection();
        guest.closeConnection();
    }

    //Attend le prochain message avec un timeout pour ne pas bloquer la suite de tests
    private String receive(NetworkManager client){
        return assertTimeoutPreemptively(Duration.ofSeconds(5), client::receiveMessageFromServer);
    }

    private int createRoom(){
        host.sendMessageToServer(Commands.CREATE_ROOM+" TIC_TAC_TOE 3 host");

        String[] response=receive(host).split(" ");
        assertEquals(Commands.CREATE_ROOM.toString(), response[0]);
        int roomId=Integer.parseInt(response[1]);
        assertTrue(roomId>=0);

        assertEquals(Commands.IS_ROOM_FULL+" false 1 2", receive(host));
        return roomId;
    }

    private int createRoomAndJoin(){
        int roomId=createRoom();

        guest.sendMessageToServer(Commands.JOIN_ROOM+" "+roomId+" TIC_TAC_TOE guest");
        assertEquals(Commands.JOIN_ROOM+" true", receive(guest));

        //Les deux clients sont prévenus que la salle est pleine
        assertEquals(Commands.IS_ROOM_FULL+" true 2 2", receive(guest));
        assertEquals(Commands.IS_ROOM_FULL+" true 2 2", receive(host));
        return roomId;
    }

    //Joue un coup et vérifie que les deux clients reçoivent le broadcast
    private void playAndDrain(NetworkManager player, String playerName, int roomId, int h, int w, String pawn){
        player.sendMessageToServer(Commands.PLAY+" "+playerName+" "+roomId+" "+h+" "+w);
        String expected=Commands.PLAY+" true "+pawn+" "+h+" "+w+" 3";
        assertEquals(expected, receive(host));
        assertEquals(expected, receive(guest));
    }


    /* Gestion des salles */
    @Test
    void testCreateRoom(){
        createRoom();
    }

    @Test
    void testJoinRoom(){
        createRoomAndJoin();
    }

    @Test
    void testJoinUnknownRoom(){
        guest.sendMessageToServer(Commands.JOIN_ROOM+" -42 TIC_TAC_TOE guest");
        assertEquals(Commands.JOIN_ROOM+" false", receive(guest));
    }

    @Test
    void testGetBoardSizeAndPlayerName(){
        int roomId=createRoomAndJoin();

        host.sendMessageToServer(Commands.GET_BOARD_SIZE+" "+roomId);
        assertEquals(Commands.GET_BOARD_SIZE+" 3", receive(host));

        host.sendMessageToServer(Commands.GET_PLAYER_NAME+" "+roomId);
        assertEquals(Commands.GET_PLAYER_NAME+" host", receive(host));
    }


    /* Déroulement d'une partie */
    @Test
    void testPlayIsBroadcastToBothClients(){
        int roomId=createRoomAndJoin();
        playAndDrain(host, "host", roomId, 0, 0, "O");
    }

    @Test
    void testPlayOutOfTurn(){
        int roomId=createRoomAndJoin();

        //guest joue alors que c'est le tour de host : refus envoyé uniquement à guest
        guest.sendMessageToServer(Commands.PLAY+" guest "+roomId+" 0 0");
        assertEquals(Commands.PLAY+" false", receive(guest));
    }

    @Test
    void testFullGameUntilWin(){
        int roomId=createRoomAndJoin();

        //Victoire de host sur la première ligne
        playAndDrain(host, "host", roomId, 0, 0, "O");
        playAndDrain(guest, "guest", roomId, 1, 0, "X");
        playAndDrain(host, "host", roomId, 0, 1, "O");
        playAndDrain(guest, "guest", roomId, 1, 1, "X");
        playAndDrain(host, "host", roomId, 0, 2, "O");

        //Chaque client signale la fin de partie avec son propre nom
        host.sendMessageToServer(Commands.IS_GAME_OVER+" "+roomId+" host");
        assertEquals(Commands.IS_GAME_OVER+" true host", receive(host));

        guest.sendMessageToServer(Commands.IS_GAME_OVER+" "+roomId+" guest");
        assertEquals(Commands.IS_GAME_OVER+" true host", receive(guest));
    }

    @Test
    void testGiveUpIsBroadcast(){
        int roomId=createRoomAndJoin();

        host.sendMessageToServer(Commands.GIVE_UP+" "+roomId+" host");
        assertEquals(Commands.GIVE_UP+" host", receive(host));
        assertEquals(Commands.GIVE_UP+" host", receive(guest));
    }


    /* Robustesse du serveur */
    @Test
    void testMalformedMessageDoesNotKillConnection(){
        int roomId=createRoomAndJoin();

        //Commande inconnue puis nombre invalide : la connexion doit survivre
        host.sendMessageToServer("GARBAGE nonsense");
        host.sendMessageToServer(Commands.PLAY+" host notANumber 0 0");

        host.sendMessageToServer(Commands.GET_BOARD_SIZE+" "+roomId);
        assertEquals(Commands.GET_BOARD_SIZE+" 3", receive(host));
    }
}
