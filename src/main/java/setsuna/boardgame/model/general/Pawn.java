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

    public static Pawn toPawn(String symbol){
        switch(symbol.charAt(0)){
            case '_': return EMPTY;
            case 'X': return CROSS;
            case 'O': return CIRCLE;
            default: return null;
        }
    }
}