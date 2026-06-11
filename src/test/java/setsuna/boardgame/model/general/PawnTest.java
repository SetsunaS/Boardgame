package setsuna.boardgame.model.general;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PawnTest{
    @Test
    void testToString(){
        assertEquals("_", Pawn.EMPTY.toString());
        assertEquals("X", Pawn.CROSS.toString());
        assertEquals("O", Pawn.CIRCLE.toString());
    }

    @Test
    void testToPawn(){
        assertEquals(Pawn.EMPTY, Pawn.toPawn("_"));
        assertEquals(Pawn.CROSS, Pawn.toPawn("X"));
        assertEquals(Pawn.CIRCLE, Pawn.toPawn("O"));
    }

    @Test
    void testToPawnUnknownSymbol(){
        assertNull(Pawn.toPawn("?"));
    }

    @Test
    void testToStringToPawnRoundTrip(){
        for(Pawn pawn: Pawn.values())
            assertEquals(pawn, Pawn.toPawn(pawn.toString()));
    }
}
