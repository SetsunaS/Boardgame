package setsuna.boardgame.ai;

import setsuna.boardgame.model.games.TicTacToe;
import setsuna.boardgame.model.general.Pawn;
import setsuna.boardgame.model.general.Player;
import setsuna.boardgame.model.general.exception.InvalidMoveException;

public class TicTacToeAiPlayer extends Player{
    private TicTacToe game;

    public TicTacToeAiPlayer(String name, TicTacToe game){
        super(name, 0);
        this.game=game;
    }

    @Override
    public Pawn play(){
        try{
            int h=0, w;
            for(; h<game.getBoard().getSize(); h++)
                for(w=0; w<game.getBoard().getSize(); w++)
                    if(game.getBoard().isEmpty(h, w))
                        return game.play(h, w);
        }
        catch(InvalidMoveException e){}

        return null;
    }
}