package setsuna.boardgame.model.games;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import setsuna.boardgame.model.general.Pawn;
import setsuna.boardgame.model.general.exception.InvalidMoveException;
import setsuna.boardgame.model.general.exception.PlayerFullException;
import setsuna.boardgame.model.general.player.HumanPlayer;
import setsuna.boardgame.model.general.player.Player;

import static org.junit.jupiter.api.Assertions.*;

public class TicTacToeTest{
    private TicTacToe game;
    private Player alice;
    private Player bob;

    @BeforeEach
    void setUp() throws PlayerFullException{
        game=new TicTacToe(3);
        alice=new HumanPlayer("alice");
        bob=new HumanPlayer("bob");
        game.addPlayer(alice);
        game.addPlayer(bob);
    }

    //Joue une succession de coups (h, w) en alternant les joueurs
    private void playMoves(int[][] moves) throws InvalidMoveException{
        for(int[] move: moves) game.play(move[0], move[1]);
    }


    /* Gestion des joueurs */
    @Test
    void testInitialState(){
        assertEquals(3, game.getBoardSize());
        assertFalse(game.isGameOver());
        assertNull(game.getWinner());
        assertEquals(alice, game.getCurrentPlayer());
        assertEquals(2, game.getMaxPlayersNumber());
        assertEquals(2, game.getCurrentPlayerNumber());
        assertFalse(game.canAddPlayer());
    }

    @Test
    void testIsSelectedGame(){
        assertTrue(game.isSelectedGame(Games.TIC_TAC_TOE));
        assertFalse(game.isSelectedGame(Games.TEST));
    }

    @Test
    void testAddPlayerWhenFullThrows(){
        assertThrows(PlayerFullException.class, () -> game.addPlayer(new HumanPlayer("charlie")));
    }

    @Test
    void testCanAddPlayerOnEmptyGame(){
        TicTacToe emptyGame=new TicTacToe(3);
        assertTrue(emptyGame.canAddPlayer());
        assertEquals(0, emptyGame.getCurrentPlayerNumber());
    }

    @Test
    void testRemovePlayerByName(){
        //Un objet différent avec le même nom doit suffire (cas du serveur)
        assertTrue(game.removePlayer(new HumanPlayer("alice")));
        assertEquals(1, game.getCurrentPlayerNumber());
        assertTrue(game.canAddPlayer());
    }

    @Test
    void testRemoveUnknownPlayer(){
        assertFalse(game.removePlayer(new HumanPlayer("charlie")));
        assertEquals(2, game.getCurrentPlayerNumber());
    }

    @Test
    void testRemovePlayerTwice(){
        assertTrue(game.removePlayer(new HumanPlayer("bob")));
        assertFalse(game.removePlayer(new HumanPlayer("bob")));
    }


    /* Placement des pions */
    @Test
    void testFirstPlayerPlaysCircleSecondPlaysCross() throws InvalidMoveException{
        assertEquals(Pawn.CIRCLE, game.play(0, 0));
        assertEquals(Pawn.CROSS, game.play(1, 1));
        assertEquals(Pawn.CIRCLE, game.play(2, 2));
    }

    @Test
    void testPlayChangesCurrentPlayer() throws InvalidMoveException{
        assertEquals(alice, game.getCurrentPlayer());
        game.play(0, 0);
        assertEquals(bob, game.getCurrentPlayer());
        game.play(1, 1);
        assertEquals(alice, game.getCurrentPlayer());
    }

    @Test
    void testIsValidMove() throws InvalidMoveException{
        assertTrue(game.isValidMove(0, 0));

        //Case occupée
        game.play(0, 0);
        assertFalse(game.isValidMove(0, 0));

        //Hors limites
        assertFalse(game.isValidMove(-1, 0));
        assertFalse(game.isValidMove(0, -1));
        assertFalse(game.isValidMove(3, 0));
        assertFalse(game.isValidMove(0, 3));
    }

    @Test
    void testPlayOnOccupiedCellThrows() throws InvalidMoveException{
        game.play(0, 0);
        assertThrows(InvalidMoveException.class, () -> game.play(0, 0));
    }

    @Test
    void testPlayOutOfBoundsThrows(){
        assertThrows(InvalidMoveException.class, () -> game.play(3, 3));
    }

    @Test
    void testLastPosition() throws InvalidMoveException{
        assertEquals(-1, game.getLastHPlayed());
        assertEquals(-1, game.getLastWPlayed());

        game.play(1, 2);
        assertEquals(1, game.getLastHPlayed());
        assertEquals(2, game.getLastWPlayed());

        game.resetLastPosition();
        assertEquals(-1, game.getLastHPlayed());
        assertEquals(-1, game.getLastWPlayed());
    }


    /* Conditions de victoire */
    @Test
    void testHorizontalWin() throws InvalidMoveException{
        playMoves(new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}, {0, 2}});

        assertTrue(game.isGameOver());
        assertEquals(alice, game.getWinner());
    }

    @Test
    void testVerticalWin() throws InvalidMoveException{
        playMoves(new int[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}, {2, 0}});

        assertTrue(game.isGameOver());
        assertEquals(alice, game.getWinner());
    }

    @Test
    void testDiagonalWin() throws InvalidMoveException{
        playMoves(new int[][]{{0, 0}, {0, 1}, {1, 1}, {0, 2}, {2, 2}});

        assertTrue(game.isGameOver());
        assertEquals(alice, game.getWinner());
    }

    @Test
    void testAntiDiagonalWin() throws InvalidMoveException{
        playMoves(new int[][]{{0, 2}, {0, 0}, {1, 1}, {0, 1}, {2, 0}});

        assertTrue(game.isGameOver());
        assertEquals(alice, game.getWinner());
    }

    @Test
    void testSecondPlayerCanWin() throws InvalidMoveException{
        playMoves(new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}, {2, 2}, {1, 2}});

        assertTrue(game.isGameOver());
        assertEquals(bob, game.getWinner());
    }

    @Test
    void testDraw() throws InvalidMoveException{
        // O X O
        // O X X
        // X O O
        playMoves(new int[][]{{0, 0}, {0, 1}, {0, 2}, {1, 1}, {1, 0}, {1, 2}, {2, 1}, {2, 0}, {2, 2}});

        assertTrue(game.isGameOver());
        assertNull(game.getWinner());
    }

    @Test
    void testNoMoveAllowedAfterGameOver() throws InvalidMoveException{
        playMoves(new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}, {0, 2}});

        assertFalse(game.isValidMove(2, 2));
        assertThrows(InvalidMoveException.class, () -> game.play(2, 2));
    }


    /* Abandon */
    @Test
    void testGiveUpMakesOpponentWinner(){
        assertTrue(game.giveUp("alice"));
        assertTrue(game.isGameOver());
        assertEquals(bob, game.getWinner());
    }

    @Test
    void testGiveUpSecondPlayer(){
        assertTrue(game.giveUp("bob"));
        assertTrue(game.isGameOver());
        assertEquals(alice, game.getWinner());
    }

    @Test
    void testGiveUpUnknownPlayer(){
        assertFalse(game.giveUp("charlie"));
        assertFalse(game.isGameOver());
    }


    /* Fermeture de la partie */
    @Test
    void testCanBeClose() throws InvalidMoveException{
        assertFalse(game.canBeClose());

        playMoves(new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}, {0, 2}});
        assertFalse(game.canBeClose());

        game.removePlayer(new HumanPlayer("alice"));
        assertFalse(game.canBeClose());

        game.removePlayer(new HumanPlayer("bob"));
        assertTrue(game.canBeClose());
    }


    /* Clonage */
    @Test
    void testCloneIsIndependent() throws InvalidMoveException{
        game.play(0, 0);

        TicTacToe clone=(TicTacToe)game.clone();
        clone.play(1, 1);

        //Le coup joué sur le clone ne doit pas modifier l'original
        assertTrue(game.isValidMove(1, 1));
        assertFalse(clone.isValidMove(1, 1));
    }

    @Test
    void testCloneDoesNotResetOriginalLastPosition() throws InvalidMoveException{
        game.play(1, 2);
        game.clone();

        //Cloner ne doit pas réinitialiser lastPosition de l'original
        assertEquals(1, game.getLastHPlayed());
        assertEquals(2, game.getLastWPlayed());
    }

    @Test
    void testCloneSharesPlayers() throws InvalidMoveException{
        TicTacToe clone=(TicTacToe)game.clone();

        //La victoire sur le clone doit référencer le même objet joueur
        clone.play(0, 0);
        clone.play(1, 0);
        clone.play(0, 1);
        clone.play(1, 1);
        clone.play(0, 2);

        assertSame(alice, clone.getWinner());
        assertFalse(game.isGameOver());
    }
}
