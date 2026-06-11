package setsuna.boardgame.model.general.player;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest{
    @Test
    void testPlayerCreation(){
        Player player=new HumanPlayer("alice", 42);
        assertEquals("alice", player.getName());
        assertEquals(42, player.getScore());
    }

    @Test
    void testDefaultScoreIsZero(){
        Player player=new HumanPlayer("bob");
        assertEquals(0, player.getScore());
    }

    @Test
    void testAddScoreAccumulates(){
        Player player=new HumanPlayer("alice", 10);
        player.addScore(10);
        player.addScore(5);

        assertEquals(25, player.getScore());
    }

    @Test
    void testToString(){
        Player player=new HumanPlayer("alice", 30);
        assertEquals("Player alice have 30 points", player.toString());
    }
}
