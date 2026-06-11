package setsuna.boardgame.model.general.player;

import setsuna.boardgame.model.games.GameModel;

public abstract class Player{
    private String name;
    private int score;
    protected GameModel game;


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
    }


    /* Jeu courant */
    public void setGame(GameModel game){
        this.game=game;
    }

    /* Affichage */
    public String toString(){
        return "Player "+name+" have "+score+" points";
    }
}
