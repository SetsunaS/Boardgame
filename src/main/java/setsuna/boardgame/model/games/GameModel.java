package setsuna.boardgame.model.games;

import setsuna.boardgame.model.general.Pawn;
import setsuna.boardgame.model.general.exception.InvalidMoveException;
import setsuna.boardgame.model.general.exception.PlayerFullException;
import setsuna.boardgame.model.general.player.Player;

public interface GameModel{
    boolean canAddPlayer();
    int getMaxPlayersNumber();
    int getCurrentPlayerNumber();
    void addPlayer(Player player) throws PlayerFullException;
    boolean removePlayer(Player player);
    boolean canBeClose();

    Player getCurrentPlayer();

    int getBoardSize();
    boolean isValidMove(int h, int w);
    Pawn play(int h, int w) throws InvalidMoveException;

    boolean isGameOver();
    Player getWinner();
    boolean giveUp(String playerName);
}