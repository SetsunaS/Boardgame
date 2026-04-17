package setsuna.boardgame.model.games;

import setsuna.boardgame.model.general.Board;
import setsuna.boardgame.model.general.Pawn;
import setsuna.boardgame.model.general.Position;
import setsuna.boardgame.model.general.exception.PlayerFullException;
import setsuna.boardgame.model.general.exception.InvalidMoveException;
import setsuna.boardgame.model.general.player.Player;

public class TicTacToe implements GameModel, Cloneable{
    private static final Games selectedGame=Games.TIC_TAC_TOE;

    public static final int MAX_PLAYERS_NUMBER=2;
    private Player[] players;

    private Board board;

    private int currentPlayer=0;
    private Position lastPosition=new Position();
    private boolean isGameOver=false;


    /* Création d'un plateau de jeu */
    public TicTacToe(Player player0, Player player1, int boardSize){
        players=new Player[MAX_PLAYERS_NUMBER];
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

    @Override
    public boolean isSelectedGame(Games selectedGame){
        return selectedGame==TicTacToe.selectedGame;
    }

    @Override
    public boolean canAddPlayer(){
        return players[0]==null || players[1]==null;
    }

    @Override
    public int getMaxPlayersNumber(){
        return MAX_PLAYERS_NUMBER;
    }

    @Override
    public int getCurrentPlayerNumber(){
        int playerNumber=0;
        if(players[0]!=null) playerNumber++;
        if(players[1]!=null) playerNumber++;
        return playerNumber;
    }

    @Override
    public void addPlayer(Player player) throws PlayerFullException{
        if(players[0]==null) players[0]=player;
        else if(players[1]==null) players[1]=player;
        else throw new PlayerFullException();
    }

    @Override
    public boolean removePlayer(Player player){
        if(players[0]==player){
            players[0]=null;
            return true;
        }
        if(players[1]==player){
            players[1]=null;
            return true;
        }
        return false;
    }

    @Override
    public boolean canBeClose(){
        return isGameOver && players[0]==null && players[1]==null;
    }


    /* Joueur courant */
    @Override
    public Player getCurrentPlayer(){
        if(currentPlayer==0) return players[0];
        if(currentPlayer==1) return players[1];
        return null;
    }

    private void changeCurrentPlayer(){
        if(currentPlayer==0) currentPlayer=1;
        else if(currentPlayer==1) currentPlayer=0;
    }

    private void setDraw(){
        currentPlayer=2;
    }


    /* Placement des pions */
    @Override
    public int getBoardSize(){
        return board.getSize();
    }

    @Override
    public boolean isValidMove(int h, int w){
        return !isGameOver && board.isInBounds(h, w) && board.isEmpty(h, w);
    }

    private void oneMove(int h, int w, Pawn pawnToSet) throws InvalidMoveException{
        //Vérification de la légalité du mouvement
        if(!isValidMove(h, w)) throw new InvalidMoveException();

        //Placer le pion
        board.setPawn(pawnToSet, h, w);
    }


    /* Enchainement d'un tour */
    @Override
    public Pawn play(int h, int w) throws InvalidMoveException{
        Pawn pawn=(currentPlayer==0)? Pawn.CIRCLE : Pawn.CROSS;
        oneMove(h, w, pawn);
        lastPosition.setPosition(h, w);

        //Vérifie si une partie est finie ou non
        checkGameOver(h, w, pawn);

        //Si non, on change de joueur pour continuer
        if(!isGameOver) changeCurrentPlayer();

        //Retourne la pièce jouée
        return pawn;
    }


    /* Jeu fini */
    private void setGameOver(){
        isGameOver=true;
    }

    @Override
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

    @Override
    public Player getWinner(){
        if(currentPlayer==0) return players[0];
        if(currentPlayer==1) return players[1];
        return null;
    }

    @Override
    public boolean giveUp(String playerName){
        if(players[0].getName().equals(playerName)) currentPlayer=1;
        else if(players[1].getName().equals(playerName)) currentPlayer=0;
        else return false;

        setGameOver();
        return true;
    }

    public int getLastHPlayed(){
        return lastPosition.getH();
    }

    public int getLastWPlayed(){
        return lastPosition.getW();
    }

    public void resetLastPosition(){
        lastPosition.reset();
    }


    /* Simulation d'un tour */
    @Override
    public Object clone(){
        try{
            //Copie superficielle
            TicTacToe clone=(TicTacToe)super.clone();

            //Copie en profondeur des objets qu'il ne faut pas modifier dans la version originale
            clone.board=new Board(board);
            lastPosition=new Position();
            return clone;
        }
        catch(CloneNotSupportedException e){
            e.printStackTrace();
            return null;
        }
    }
}