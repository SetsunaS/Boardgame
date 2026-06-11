package setsuna.boardgame.model.general.player.ai;

import setsuna.boardgame.model.games.TicTacToe;
import setsuna.boardgame.model.general.Pawn;
import setsuna.boardgame.model.general.Position;
import setsuna.boardgame.model.general.exception.InvalidMoveException;
import setsuna.boardgame.model.general.player.Player;

public class TicTacToeAiPlayer extends Player{

    /* Création d'un joueur */
    public TicTacToeAiPlayer(String name, int score){
        super(name, score);
    }

    public TicTacToeAiPlayer(String name){
        this(name, 0);
    }

    public Pawn play(){
        try{
            Position bestMove=findBestMove();
            return game.play(bestMove.getH(), bestMove.getW());
        }
        catch(InvalidMoveException e){}

        return null;
    }

    private Position findBestMove(){
        int maxScore=Integer.MIN_VALUE, score;
        Position position=new Position();

        //Simulation de coups sur une copie
        int gameSize=game.getBoardSize();
        for(int h=0; h<gameSize; h++){
            for(int w=0; w<gameSize; w++){
                if(game.isValidMove(h, w)){
                    try{
                        TicTacToe gameCopy=(TicTacToe)((TicTacToe)game).clone();

                        gameCopy.play(h, w);
                        score=minimax(gameCopy, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);

                        //Meilleur coup
                        if(score>maxScore){
                            maxScore=score;
                            position.setPosition(h, w);
                        }
                    }
                    catch(InvalidMoveException e){}
                }
            }
        }

        return position;
    }

    //Cherche à maximiser le score de l'IA et minimiser le score du joueur humain
    //Ajout en plus de l'élagage alpha-bêta pour arrêter la recherche d'un meilleur score si on en a un déjà suffisant
    private int minimax(TicTacToe game, int depth, boolean isMaximizingPlayer, int alpha, int beta){
        int MAX_DEPTH=15/game.getBoardSize();

        //Fin de la simulation : +10 points si l'IA gagne, -10 s'il perd, 0 en cas d'égalité
        //Valorisation des victoires rapides et des défaites lentes
        if(depth>MAX_DEPTH || game.isGameOver()){
            if(game.getWinner()==this) return 10-depth;
            if(game.getWinner()==null) return 0;
            return -10+depth;
        }

        int bestScore;
        int gameSize=game.getBoardSize();

        //Tour de l'ia : on cherche à maximiser bestScore
        if(isMaximizingPlayer){
            bestScore=Integer.MIN_VALUE;

            for(int h=0; h<gameSize; h++){
                for(int w=0; w<gameSize; w++){
                    if(game.isValidMove(h, w)){
                        try{
                            TicTacToe gameCopy=(TicTacToe)game.clone();
                            gameCopy.play(h, w);

                            //Garde le meilleur score
                            bestScore=Math.max(bestScore, minimax(gameCopy, depth+1, false, alpha, beta));
                            alpha=Math.max(alpha, bestScore);

                            //Elagage : on a un résultat satisfaisant, on décide de s'arrêter là
                            if(alpha>beta) return bestScore;
                        }
                        catch(InvalidMoveException e){}
                    }
                }
            }
        }

        //Tour du joueur humain : on cherche à minimiser bestScore
        else{
            bestScore=Integer.MAX_VALUE;

            for(int h=0; h<gameSize; h++){
                for(int w=0; w<gameSize; w++){
                    if(game.isValidMove(h, w)){
                        try{
                            TicTacToe gameCopy=(TicTacToe)game.clone();
                            gameCopy.play(h, w);

                            //Garde le moins bon score
                            bestScore=Math.min(bestScore, minimax(gameCopy, depth+1, true, alpha, beta));
                            beta=Math.min(beta, bestScore);

                            //Elagage : on a un résultat satisfaisant, on décide de s'arrêter là
                            if(alpha>beta) return bestScore;
                        }
                        catch(InvalidMoveException e){}
                    }
                }
            }
        }

        return bestScore;
    }
}