package setsuna.boardgame.model.general.player.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import setsuna.boardgame.model.games.TicTacToe;
import setsuna.boardgame.model.general.Pawn;
import setsuna.boardgame.model.general.exception.InvalidMoveException;
import setsuna.boardgame.model.general.exception.PlayerFullException;
import setsuna.boardgame.model.general.player.HumanPlayer;

import static org.junit.jupiter.api.Assertions.*;

public class TicTacToeAiPlayerTest{
    private TicTacToe game;
    private HumanPlayer human;
    private TicTacToeAiPlayer ai;

    @BeforeEach
    void setUp() throws PlayerFullException{
        game=new TicTacToe(3);
        human=new HumanPlayer("human");
        ai=new TicTacToeAiPlayer("ai");

        //L'humain joue en premier (CIRCLE), l'ia en second (CROSS)
        game.addPlayer(human);
        game.addPlayer(ai);
        ai.setGame(game);
    }

    @Test
    void testAiPlaysValidMove() throws InvalidMoveException{
        game.play(0, 0);
        Pawn pawn=ai.play();

        assertEquals(Pawn.CROSS, pawn);
        assertNotEquals(-1, game.getLastHPlayed());
        assertNotEquals(-1, game.getLastWPlayed());
    }

    @Test
    void testAiTakesWinningMove() throws InvalidMoveException{
        // O . .        L'ia (X) a deux pions ligne 1 : elle doit gagner en (1,2)
        // X X .
        // . . O
        game.play(0, 0); //O
        game.play(1, 0); //X
        game.play(0, 1); //O
        game.play(1, 1); //X
        game.play(2, 2); //O

        ai.play();

        assertTrue(game.isGameOver());
        assertSame(ai, game.getWinner());
    }

    @Test
    void testAiBlocksOpponentWin() throws InvalidMoveException{
        // O O .        L'humain (O) menace en (0,2) : l'ia doit bloquer
        // . X .
        // . . .
        game.play(0, 0); //O
        game.play(1, 1); //X
        game.play(0, 1); //O

        ai.play();

        assertEquals(0, game.getLastHPlayed());
        assertEquals(2, game.getLastWPlayed());
        assertFalse(game.isGameOver());
    }

    @Test
    void testAiPrefersWinningOverBlocking() throws InvalidMoveException{
        // O O .        L'humain menace en (0,2) mais l'ia peut gagner en (1,2) :
        // X X .        gagner vaut mieux que bloquer
        // O . .
        game.play(0, 0); //O
        game.play(1, 0); //X
        game.play(0, 1); //O
        game.play(1, 1); //X
        game.play(2, 0); //O

        ai.play();

        assertTrue(game.isGameOver());
        assertSame(ai, game.getWinner());
    }
}
