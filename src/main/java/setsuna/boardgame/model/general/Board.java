package setsuna.boardgame.model.general;

public class Board{
    private Pawn[][] board;


    /* Création de plateau */
    private Board(int height, int width){
        board=new Pawn[height][width];
        for(int h=0; h<height; h++)
            for(int w=0; w<width; w++)
                board[h][w]=Pawn.EMPTY;
    }

    public Board(int size){
        this(size, size);
    }


    /* Copie */
    public Board(Board boardToCopy){
        int size=boardToCopy.getSize();
        board=new Pawn[size][size];
        for(int h=0; h<size; h++)
            for(int w=0; w<size; w++)
                board[h][w]=boardToCopy.board[h][w];
    }


    /* Etat du plateau */
    public int getSize(){
        return board.length;
    }

    public boolean isInBounds(int h, int w){
        return h>=0 && h<board.length && w>=0 && w<board[0].length;
    }

    public boolean isEmpty(int h, int w){
        return board[h][w]==Pawn.EMPTY;
    }

    public boolean isFull(){
        for(int h=0; h<board.length; h++){
            for(int w=0; w<board[h].length; w++)
                if(board[h][w]==Pawn.EMPTY) return false;
        }
        return true;
    }


    /* Placement des pions */
    public void setPawn(Pawn pawn, int h, int w){
        board[h][w]=pawn;
    }

    public boolean isPawn(int h, int w, Pawn pawn){
        return board[h][w]==pawn;
    }


    /* Affichage */
    public String toString(){
        StringBuilder stringBuilder=new StringBuilder();
        for(int h=0; h<board.length; h++){
            for(int w=0; w<board[h].length; w++)
                stringBuilder.append(board[h][w]+" ");
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }
}