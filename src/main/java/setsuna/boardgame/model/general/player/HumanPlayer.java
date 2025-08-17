package setsuna.boardgame.model.general.player;

import setsuna.boardgame.model.general.Pawn;

public class HumanPlayer extends Player{
    /* Création d'un joueur */
    public HumanPlayer(String name, int score){
        super(name, score);
    }

    public HumanPlayer(String name){
        this(name, 0);
    }

    /* Jouer */
    @Override
    public Pawn play(){
        return null;
    }
}