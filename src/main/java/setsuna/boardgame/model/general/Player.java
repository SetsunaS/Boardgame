package setsuna.boardgame.model.general;

import setsuna.boardgame.database.DatabaseManager;

public class Player{
    private String name;
    private int score;


    /* Création d'un joueur */
    public Player(String name, int score){
        this.name=name;
        this.score=score;
    }

    public Player(String name){
        this(name, 0);
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


    /* Affichage */
    public String toString(){
        return "Player "+name+" have "+score+" points";
    }
}