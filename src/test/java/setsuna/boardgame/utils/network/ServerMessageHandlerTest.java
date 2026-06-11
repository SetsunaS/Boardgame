package setsuna.boardgame.utils.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import setsuna.boardgame.model.general.Pawn;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServerMessageHandlerTest{
    private static final int ROOM_ID=777;

    private FakeNetworkManager networkManager;
    private RecordingListener listener;
    private ServerMessageHandler handler;

    //Capture les messages sortants au lieu d'ouvrir une vraie connexion
    private static class FakeNetworkManager extends NetworkManager{
        private final List<String> sentMessages=new ArrayList<>();
        private boolean connectionClosed=false;

        @Override
        public void sendMessageToServer(String message){
            sentMessages.add(message);
        }

        @Override
        public void closeConnection(){
            connectionClosed=true;
        }
    }

    //Enregistre les appels de l'interface sous forme de texte
    private static class RecordingListener implements GameClientListener{
        private final List<String> events=new ArrayList<>();

        @Override
        public void onRoomWaiting(int currentPlayerNumber, int maxPlayersNumber){
            events.add("roomWaiting "+currentPlayerNumber+"/"+maxPlayersNumber);
        }

        @Override
        public void onRoomFull(){
            events.add("roomFull");
        }

        @Override
        public void onCurrentPlayer(String playerName, boolean isMyTurn){
            events.add("currentPlayer "+playerName+" "+isMyTurn);
        }

        @Override
        public void onBoardCreation(int boardSize){
            events.add("boardCreation "+boardSize);
        }

        @Override
        public void onMovePlayed(Pawn pawn, int h, int w, int boardSize){
            events.add("movePlayed "+pawn+" "+h+" "+w+" "+boardSize);
        }

        @Override
        public void onGameOver(String winner, boolean isWinner){
            events.add("gameOver "+winner+" "+isWinner);
        }
    }

    @BeforeEach
    void setUp(){
        networkManager=new FakeNetworkManager();
        listener=new RecordingListener();
        handler=new ServerMessageHandler(networkManager, listener, "me", ROOM_ID);
    }


    /* Salle d'attente */
    @Test
    void testRoomNotFull(){
        handler.handleServerMessage("IS_ROOM_FULL false 1 2");

        assertEquals(List.of("roomWaiting 1/2"), listener.events);
        assertTrue(networkManager.sentMessages.isEmpty());
    }

    @Test
    void testRoomFullAsksCurrentPlayer(){
        handler.handleServerMessage("IS_ROOM_FULL true 2 2");

        assertEquals(List.of("roomFull"), listener.events);
        assertEquals(List.of("GET_PLAYER_NAME "+ROOM_ID), networkManager.sentMessages);
    }


    /* Joueur courant */
    @Test
    void testCurrentPlayerIsMe(){
        handler.handleServerMessage("GET_PLAYER_NAME me");
        assertEquals(List.of("currentPlayer me true"), listener.events);
    }

    @Test
    void testCurrentPlayerIsOpponent(){
        handler.handleServerMessage("GET_PLAYER_NAME opponent");
        assertEquals(List.of("currentPlayer opponent false"), listener.events);
    }


    /* Création du plateau */
    @Test
    void testBoardCreation(){
        handler.handleServerMessage("GET_BOARD_SIZE 3");
        assertEquals(List.of("boardCreation 3"), listener.events);
    }

    @Test
    void testBoardCreationWithInvalidSize(){
        handler.handleServerMessage("GET_BOARD_SIZE -1");
        assertTrue(listener.events.isEmpty());
    }


    /* Coups joués */
    @Test
    void testValidMoveAsksGameOver(){
        handler.handleServerMessage("PLAY true O 0 1 3");

        assertEquals(List.of("movePlayed O 0 1 3"), listener.events);
        assertEquals(List.of("IS_GAME_OVER "+ROOM_ID+" me"), networkManager.sentMessages);
    }

    @Test
    void testInvalidMoveIsIgnored(){
        handler.handleServerMessage("PLAY false");

        assertTrue(listener.events.isEmpty());
        assertTrue(networkManager.sentMessages.isEmpty());
    }


    /* Fin de partie */
    @Test
    void testGameOverWhenIWin(){
        handler.handleServerMessage("IS_GAME_OVER true me");

        assertEquals(List.of("gameOver me true"), listener.events);
        assertTrue(networkManager.connectionClosed);
    }

    @Test
    void testGameOverWhenOpponentWins(){
        handler.handleServerMessage("IS_GAME_OVER true opponent");

        assertEquals(List.of("gameOver opponent false"), listener.events);
        assertTrue(networkManager.connectionClosed);
    }

    @Test
    void testGameOverWithDraw(){
        handler.handleServerMessage("IS_GAME_OVER true null");

        assertEquals(List.of("gameOver null false"), listener.events);
        assertTrue(networkManager.connectionClosed);
    }

    @Test
    void testGameNotOverAsksNextPlayer(){
        handler.handleServerMessage("IS_GAME_OVER false null");

        assertTrue(listener.events.isEmpty());
        assertEquals(List.of("GET_PLAYER_NAME "+ROOM_ID), networkManager.sentMessages);
        assertFalse(networkManager.connectionClosed);
    }


    /* Abandon */
    @Test
    void testGiveUpAsksGameOverWithOwnName(){
        handler.handleServerMessage("GIVE_UP opponent");

        assertTrue(listener.events.isEmpty());
        assertEquals(List.of("IS_GAME_OVER "+ROOM_ID+" me"), networkManager.sentMessages);
    }


    /* Messages gérés ailleurs */
    @Test
    void testCreateRoomAndJoinRoomAreIgnored(){
        handler.handleServerMessage("CREATE_ROOM 777");
        handler.handleServerMessage("JOIN_ROOM true");

        assertTrue(listener.events.isEmpty());
        assertTrue(networkManager.sentMessages.isEmpty());
    }
}
