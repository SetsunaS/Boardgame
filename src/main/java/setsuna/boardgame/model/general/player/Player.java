package setsuna.boardgame.model.general.player;

import setsuna.boardgame.utils.database.DatabaseManager;
import setsuna.boardgame.model.games.TicTacToe;
import setsuna.boardgame.model.general.Pawn;

public abstract class Player{
    private String name;
    private int score;
    protected TicTacToe game;


    /* Création d'un joueur */
    public Player(String name, int score){
        this.name=name;
        this.score=score;
    }


    /* Getter */
    public String getName(){
        return name;
    }

    public int getScore(){
        return score;
    }


    /* Score */
    public void addScore(int scoreToAdd){
        this.score+=scoreToAdd;
        DatabaseManager.updatePlayerScore(name, score);
    }


    /* Jouer */
    public abstract Pawn play();

    public void setGame(TicTacToe game){
        this.game=game;
    }

    /* Affichage */
    public String toString(){
        return "Player "+name+" have "+score+" points";
    }
}