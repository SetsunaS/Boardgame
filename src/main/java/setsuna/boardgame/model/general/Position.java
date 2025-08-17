package setsuna.boardgame.model.general;

public class Position{
    private int h, w;

    public Position(){
        h=-1;
        w=-1;
    }

    public void setPosition(int h, int w){
        this.h=h;
        this.w=w;
    }

    public int getH(){
        return h;
    }

    public int getW(){
        return w;
    }

    public void reset(){
        setPosition(-1, -1);
    }
}