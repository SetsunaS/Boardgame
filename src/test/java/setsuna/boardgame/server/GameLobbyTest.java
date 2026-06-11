package setsuna.boardgame.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import setsuna.boardgame.model.games.Games;
import setsuna.boardgame.model.games.TicTacToe;
import setsuna.boardgame.model.general.exception.InvalidMoveException;
import setsuna.boardgame.model.general.exception.PlayerFullException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

public class GameLobbyTest{
    private GameLobby lobby;
    private StringWriter hostOutput;
    private int roomId;

    private PrintWriter newWriter(){
        return new PrintWriter(new StringWriter(), true);
    }

    @BeforeEach
    void setUp() throws PlayerFullException{
        lobby=new GameLobby();
        hostOutput=new StringWriter();
        roomId=lobby.createRoom(new TicTacToe(3), "host", new PrintWriter(hostOutput, true));
    }

    /* Création et jonction de salles */
    @Test
    void testCreateRoom(){
        assertTrue(roomId>=0);
        assertNotNull(lobby.getGameSession(roomId));
    }

    @Test
    void testCreateRoomIdsAreUnique() throws PlayerFullException{
        int secondRoomId=lobby.createRoom(new TicTacToe(3), "host2", newWriter());
        assertNotEquals(roomId, secondRoomId);
    }

    @Test
    void testCreateRoomWithNullModel() throws PlayerFullException{
        assertEquals(-1, lobby.createRoom(null, "host", newWriter()));
    }

    @Test
    void testJoinRoom() throws PlayerFullException{
        assertTrue(lobby.joinRoom(roomId, Games.TIC_TAC_TOE, "guest", newWriter()));
    }

    @Test
    void testJoinUnknownRoom() throws PlayerFullException{
        assertFalse(lobby.joinRoom(-42, Games.TIC_TAC_TOE, "guest", newWriter()));
    }

    @Test
    void testIsRoomFull() throws PlayerFullException{
        assertEquals("false 1 2", lobby.isRoomFull(roomId));

        lobby.joinRoom(roomId, Games.TIC_TAC_TOE, "guest", newWriter());
        assertEquals("true 2 2", lobby.isRoomFull(roomId));
    }

    @Test
    void testIsRoomFullUnknownRoom(){
        assertNull(lobby.isRoomFull(-42));
    }

    @Test
    void testGetPlayerNameAndBoardSize(){
        assertEquals("host", lobby.getPlayerName(roomId));
        assertEquals(3, lobby.getBoardSize(roomId));

        assertNull(lobby.getPlayerName(-42));
        assertEquals(-1, lobby.getBoardSize(-42));
    }


    /* Déroulement d'une partie */
    @Test
    void testPlayInTurn() throws PlayerFullException, InvalidMoveException{
        lobby.joinRoom(roomId, Games.TIC_TAC_TOE, "guest", newWriter());

        assertEquals("true O 0 0 3", lobby.play("host", roomId, 0, 0));
        assertEquals("true X 1 1 3", lobby.play("guest", roomId, 1, 1));
    }

    @Test
    void testPlayOutOfTurn() throws PlayerFullException, InvalidMoveException{
        lobby.joinRoom(roomId, Games.TIC_TAC_TOE, "guest", newWriter());

        assertEquals("false", lobby.play("guest", roomId, 0, 0));
    }

    @Test
    void testPlayOnOccupiedCell() throws PlayerFullException, InvalidMoveException{
        lobby.joinRoom(roomId, Games.TIC_TAC_TOE, "guest", newWriter());

        lobby.play("host", roomId, 0, 0);
        assertEquals("false", lobby.play("guest", roomId, 0, 0));
    }

    @Test
    void testPlayUnknownRoom() throws InvalidMoveException{
        assertEquals("false", lobby.play("host", -42, 0, 0));
    }

    @Test
    void testBroadcast() throws PlayerFullException{
        lobby.broadcast(roomId, "HELLO");
        assertTrue(hostOutput.toString().contains("HELLO"));
    }


    /* Fin de partie et suppression de la salle */
    @Test
    void testIsGameOverBeforeEnd(){
        assertEquals("false null", lobby.isGameOver(roomId, "host"));
        assertNotNull(lobby.getGameSession(roomId));
    }

    @Test
    void testIsGameOverUnknownRoom(){
        assertNull(lobby.isGameOver(-42, "host"));
    }

    @Test
    void testGiveUp() throws PlayerFullException{
        lobby.joinRoom(roomId, Games.TIC_TAC_TOE, "guest", newWriter());

        assertTrue(lobby.giveUp(roomId, "host"));
        assertFalse(lobby.giveUp(-42, "host"));
    }

    @Test
    void testRoomIsDeletedAfterBothPlayersAcknowledgeGameOver() throws PlayerFullException, InvalidMoveException{
        lobby.joinRoom(roomId, Games.TIC_TAC_TOE, "guest", newWriter());

        //Victoire de host sur la première ligne
        lobby.play("host", roomId, 0, 0);
        lobby.play("guest", roomId, 1, 0);
        lobby.play("host", roomId, 0, 1);
        lobby.play("guest", roomId, 1, 1);
        lobby.play("host", roomId, 0, 2);

        //Chaque client signale la fin de partie : la salle doit être supprimée après le second
        assertEquals("true host", lobby.isGameOver(roomId, "host"));
        assertNotNull(lobby.getGameSession(roomId));

        assertEquals("true host", lobby.isGameOver(roomId, "guest"));
        assertNull(lobby.getGameSession(roomId));
    }

    @Test
    void testRoomIsDeletedAfterGiveUp() throws PlayerFullException{
        lobby.joinRoom(roomId, Games.TIC_TAC_TOE, "guest", newWriter());
        lobby.giveUp(roomId, "host");

        assertEquals("true guest", lobby.isGameOver(roomId, "host"));
        assertEquals("true guest", lobby.isGameOver(roomId, "guest"));
        assertNull(lobby.getGameSession(roomId));
    }
}
