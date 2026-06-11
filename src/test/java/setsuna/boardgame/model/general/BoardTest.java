package setsuna.boardgame.model.general;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest{
    @Test
    void testNewBoardIsEmpty(){
        Board board=new Board(3);
        for(int h=0; h<3; h++)
            for(int w=0; w<3; w++)
                assertTrue(board.isEmpty(h, w));
    }

    @Test
    void testGetSize(){
        assertEquals(3, new Board(3).getSize());
        assertEquals(5, new Board(5).getSize());
    }

    @Test
    void testSetPawn(){
        Board board=new Board(3);
        board.setPawn(Pawn.CROSS, 1, 2);

        assertFalse(board.isEmpty(1, 2));
        assertTrue(board.isPawn(1, 2, Pawn.CROSS));
        assertFalse(board.isPawn(1, 2, Pawn.CIRCLE));
    }

    @Test
    void testIsInBounds(){
        Board board=new Board(3);

        assertTrue(board.isInBounds(0, 0));
        assertTrue(board.isInBounds(2, 2));
        assertTrue(board.isInBounds(0, 2));
        assertTrue(board.isInBounds(2, 0));

        assertFalse(board.isInBounds(-1, 0));
        assertFalse(board.isInBounds(0, -1));
        assertFalse(board.isInBounds(3, 0));
        assertFalse(board.isInBounds(0, 3));
    }

    @Test
    void testIsFull(){
        Board board=new Board(2);
        assertFalse(board.isFull());

        board.setPawn(Pawn.CROSS, 0, 0);
        board.setPawn(Pawn.CIRCLE, 0, 1);
        board.setPawn(Pawn.CROSS, 1, 0);
        assertFalse(board.isFull());

        board.setPawn(Pawn.CIRCLE, 1, 1);
        assertTrue(board.isFull());
    }

    @Test
    void testCopyIsIndependent(){
        Board original=new Board(3);
        original.setPawn(Pawn.CROSS, 0, 0);

        Board copy=new Board(original);
        assertTrue(copy.isPawn(0, 0, Pawn.CROSS));

        //Modifier l'original ne doit pas modifier la copie
        original.setPawn(Pawn.CIRCLE, 1, 1);
        assertTrue(copy.isEmpty(1, 1));

        //Modifier la copie ne doit pas modifier l'original
        copy.setPawn(Pawn.CIRCLE, 2, 2);
        assertTrue(original.isEmpty(2, 2));
    }

    @Test
    void testToString(){
        Board board=new Board(2);
        board.setPawn(Pawn.CROSS, 0, 0);
        board.setPawn(Pawn.CIRCLE, 1, 1);

        assertEquals("X _ \n_ O \n", board.toString());
    }
}
