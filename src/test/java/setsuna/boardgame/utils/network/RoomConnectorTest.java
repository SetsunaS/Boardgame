package setsuna.boardgame.utils.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import setsuna.boardgame.model.games.Games;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class RoomConnectorTest{
    private FakeNetworkManager networkManager;
    private RecordingListener listener;
    private RoomConnector connector;

    //Simule la connexion et renvoie une réponse préparée au lieu d'ouvrir une vraie connexion
    private static class FakeNetworkManager extends NetworkManager{
        private final List<String> sentMessages=new CopyOnWriteArrayList<>();
        private String serverResponse;
        private boolean connected=false;

        @Override
        public void connectToServer(){
            connected=true;
        }

        @Override
        public void sendMessageToServer(String message){
            sentMessages.add(message);
        }

        @Override
        public String receiveMessageFromServer(){
            return serverResponse;
        }
    }

    private static class RecordingListener implements RoomConnectionListener{
        private final List<String> events=new CopyOnWriteArrayList<>();
        private final CountDownLatch latch=new CountDownLatch(1);

        @Override
        public void onRoomEntered(int roomId){
            events.add("entered "+roomId);
            latch.countDown();
        }

        @Override
        public void onRoomJoinFailed(){
            events.add("joinFailed");
            latch.countDown();
        }
    }

    @BeforeEach
    void setUp(){
        networkManager=new FakeNetworkManager();
        listener=new RecordingListener();
        connector=new RoomConnector(networkManager, listener);
    }

    //Attend la réponse traitée dans le thread du connecteur
    private void awaitListener() throws InterruptedException{
        assertTrue(listener.latch.await(5, TimeUnit.SECONDS), "Listener was never notified.");
    }


    /* Traitement des réponses du serveur */
    @Test
    void testCreateRoomResponseEntersRoom(){
        connector.handleCreateRoomResponse("CREATE_ROOM 777");
        assertEquals(List.of("entered 777"), listener.events);
    }

    @Test
    void testCreateRoomResponseWithFailure(){
        connector.handleCreateRoomResponse("CREATE_ROOM -1");
        assertTrue(listener.events.isEmpty());
    }

    @Test
    void testJoinRoomResponseEntersRoom(){
        connector.handleJoinRoomResponse(777, "JOIN_ROOM true");
        assertEquals(List.of("entered 777"), listener.events);
    }

    @Test
    void testJoinRoomResponseWithFailure(){
        connector.handleJoinRoomResponse(777, "JOIN_ROOM false");
        assertEquals(List.of("joinFailed"), listener.events);
    }


    /* Flux complet : connexion, envoi, traitement de la réponse */
    @Test
    void testCreateRoomFullFlow() throws Exception{
        networkManager.serverResponse="CREATE_ROOM 777";
        connector.createRoom(Games.TIC_TAC_TOE, 3, "me");
        awaitListener();

        assertTrue(networkManager.connected);
        assertEquals(List.of("CREATE_ROOM TIC_TAC_TOE 3 me"), networkManager.sentMessages);
        assertEquals(List.of("entered 777"), listener.events);
    }

    @Test
    void testJoinRoomFullFlow() throws Exception{
        networkManager.serverResponse="JOIN_ROOM true";
        connector.joinRoom(777, Games.TIC_TAC_TOE, "me");
        awaitListener();

        assertTrue(networkManager.connected);
        assertEquals(List.of("JOIN_ROOM 777 TIC_TAC_TOE me"), networkManager.sentMessages);
        assertEquals(List.of("entered 777"), listener.events);
    }

    @Test
    void testJoinRoomFullFlowWithFailure() throws Exception{
        networkManager.serverResponse="JOIN_ROOM false";
        connector.joinRoom(777, Games.TIC_TAC_TOE, "me");
        awaitListener();

        assertEquals(List.of("joinFailed"), listener.events);
    }
}
