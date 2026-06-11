package setsuna.boardgame.model.general;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PositionTest{
    @Test
    void testDefaultPositionIsInvalid(){
        Position position=new Position();
        assertEquals(-1, position.getH());
        assertEquals(-1, position.getW());
    }

    @Test
    void testSetPosition(){
        Position position=new Position();
        position.setPosition(1, 2);

        assertEquals(1, position.getH());
        assertEquals(2, position.getW());
    }

    @Test
    void testReset(){
        Position position=new Position();
        position.setPosition(1, 2);
        position.reset();

        assertEquals(-1, position.getH());
        assertEquals(-1, position.getW());
    }
}
