package setsuna.boardgame.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import setsuna.boardgame.model.games.Games;
import setsuna.boardgame.model.games.TicTacToe;
import setsuna.boardgame.model.general.Pawn;
import setsuna.boardgame.model.general.exception.InvalidMoveException;
import setsuna.boardgame.model.general.exception.PlayerFullException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

public class GameSessionTest{
    private GameSession session;
    private StringWriter hostOutput;

    @BeforeEach
    void setUp() throws PlayerFullException{
        hostOutput=new StringWriter();
        session=new GameSession(new TicTacToe(3), "host", new PrintWriter(hostOutput, true));
    }

    @Test
    void testNewSessionHasHostOnly(){
        assertEquals(1, session.getCurrentPlayerNumber());
        assertEquals(2, session.getMaxPlayersNumber());
        assertTrue(session.canAddPlayer());
        assertEquals("host", session.getCurrentPlayerName());
    }

    @Test
    void testJoinRoom() throws PlayerFullException{
        boolean isJoin=session.joinRoom(Games.TIC_TAC_TOE, "guest", new PrintWriter(new StringWriter(), true));

        assertTrue(isJoin);
        assertEquals(2, session.getCurrentPlayerNumber());
        assertFalse(session.canAddPlayer());
    }

    @Test
    void testJoinRoomWithWrongGame() throws PlayerFullException{
        boolean isJoin=session.joinRoom(Games.TEST, "guest", new PrintWriter(new StringWriter(), true));

        assertFalse(isJoin);
        assertEquals(1, session.getCurrentPlayerNumber());
    }

    @Test
    void testJoinFullRoom() throws PlayerFullException{
        session.joinRoom(Games.TIC_TAC_TOE, "guest", new PrintWriter(new StringWriter(), true));
        boolean isJoin=session.joinRoom(Games.TIC_TAC_TOE, "intruder", new PrintWriter(new StringWriter(), true));

        assertFalse(isJoin);
    }

    @Test
    void testBroadcastReachesAllClients() throws PlayerFullException{
        StringWriter guestOutput=new StringWriter();
        session.joinRoom(Games.TIC_TAC_TOE, "guest", new PrintWriter(guestOutput, true));

        session.broadcast("PLAY true O 0 0 3");

        assertTrue(hostOutput.toString().contains("PLAY true O 0 0 3"));
        assertTrue(guestOutput.toString().contains("PLAY true O 0 0 3"));
    }

    @Test
    void testPlay() throws PlayerFullException, InvalidMoveException{
        session.joinRoom(Games.TIC_TAC_TOE, "guest", new PrintWriter(new StringWriter(), true));

        assertTrue(session.isValidMove(0, 0));
        assertEquals(Pawn.CIRCLE, session.play(0, 0));
        assertFalse(session.isValidMove(0, 0));
        assertEquals("guest", session.getCurrentPlayerName());
    }

    @Test
    void testGetBoardSize(){
        assertEquals(3, session.getBoardSize());
    }

    @Test
    void testGiveUpAndWinner() throws PlayerFullException{
        session.joinRoom(Games.TIC_TAC_TOE, "guest", new PrintWriter(new StringWriter(), true));

        assertTrue(session.giveUp("host"));
        assertTrue(session.isGameOver());
        assertEquals("guest", session.getWinner());
    }

    @Test
    void testWinnerIsNullBeforeGameOver(){
        assertNull(session.getWinner());
    }

    @Test
    void testRemovePlayerAndCanBeClose() throws PlayerFullException{
        session.joinRoom(Games.TIC_TAC_TOE, "guest", new PrintWriter(new StringWriter(), true));
        session.giveUp("host");

        assertFalse(session.canBeClose());

        assertTrue(session.removePlayer("host"));
        assertTrue(session.removePlayer("guest"));
        assertTrue(session.canBeClose());
    }
}
