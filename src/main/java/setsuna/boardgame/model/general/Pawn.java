package setsuna.boardgame.model.general;

public enum Pawn{
    EMPTY('_'), CROSS('X'), CIRCLE('O');

    private char symbol;

    Pawn(char symbol){
        this.symbol=symbol;
    }

    public String toString(){
        return Character.toString(symbol);
    }
}