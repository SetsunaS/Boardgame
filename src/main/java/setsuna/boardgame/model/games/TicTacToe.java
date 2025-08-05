package setsuna.boardgame.model.games;

import setsuna.boardgame.model.general.Board;
import setsuna.boardgame.model.general.Pawn;
import setsuna.boardgame.model.general.Player;
import setsuna.boardgame.model.general.exception.PlayerFullException;
import setsuna.boardgame.model.general.exception.InvalidMoveException;

public class TicTacToe{
    private Player[] players=new Player[2];
    private final Board board;

    private int currentPlayer=0;
    private boolean isGameOver=false;


    /* Création d'un plateau de jeu */
    private TicTacToe(Player player0, Player player1, int boardSize){
        players[0]=player0;
        players[1]=player1;
        this.board=new Board(boardSize);
    }

    public TicTacToe(int boardSize){
        this(null, null, boardSize);
    }

    public TicTacToe(){
        this(3);
    }

    public void addPlayer(Player player) throws PlayerFullException{
        if(players[0]==null) players[0]=player;
        else if(players[1]==null) players[1]=player;
        else throw new PlayerFullException();
    }


    /* Changement de joueur courant */
    private void changeCurrentPlayer(){
        if(currentPlayer==0) currentPlayer=1;
        else if(currentPlayer==1) currentPlayer=0;
    }

    private void setDraw(){
        currentPlayer=2;
    }


    /* Placement des pions */
    private void oneMove(int h, int w, Pawn pawnToSet) throws InvalidMoveException{
        //Vérification de la légalité du mouvement
        if(isGameOver || !board.isInBounds(h, w) || !board.isEmpty(h, w)) throw new InvalidMoveException();

        //Placer le pion
        board.setPawn(pawnToSet, h, w);
    }


    /* Jeu fini */
    private void setGameOver(){
        isGameOver=true;
    }

    public boolean isGameOver(){
        return isGameOver;
    }

    private void checkGameOver(int h, int w, Pawn pawn){
        int maxScore=board.getSize();

        //Vérification sur l'horizontale
        int currentScore=0;
        while(currentScore<maxScore && board.isPawn(h, currentScore, pawn)) currentScore++;
        if(currentScore==maxScore){
            setGameOver();
            return;
        }

        //Vérification sur la verticale
        currentScore=0;
        while(currentScore<maxScore && board.isPawn(currentScore, w, pawn)) currentScore++;
        if(currentScore==maxScore){
            setGameOver();
            return;
        }

        //Vérification sur les deux grandes diagonales
        currentScore=0;
        if(h==w){
            while(currentScore<maxScore && board.isPawn(currentScore, currentScore, pawn)) currentScore++;
            if(currentScore==maxScore){
                setGameOver();
                return;
            }
        }
        else if(h+w==maxScore-1){
            while(currentScore<maxScore && board.isPawn(currentScore, maxScore-currentScore-1, pawn)) currentScore++;
            if(currentScore==maxScore){
                setGameOver();
                return;
            }
        }

        //Egalité
        if(board.isFull()){
            setGameOver();
            setDraw();
        }
    }

    public Player getWinner(){
        if(currentPlayer==0) return players[0];
        if(currentPlayer==1) return players[1];
        return null;
    }


    /* Enchainement d'un tour */
    public Pawn play(int h, int w) throws InvalidMoveException{
        Pawn pawn=(currentPlayer==0)? Pawn.CIRCLE : Pawn.CROSS;
        oneMove(h, w, pawn);

        //Vérifie si une partie est finie ou non
        checkGameOver(h, w, pawn);

        //Si non, on change de joueur pour continuer
        if(!isGameOver) changeCurrentPlayer();

        //Retourne la pièce jouée
        return pawn;
    }
}