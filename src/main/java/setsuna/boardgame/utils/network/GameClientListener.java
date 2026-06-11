package setsuna.boardgame.utils.network;

import setsuna.boardgame.model.general.Pawn;

/** Réactions de l'interface aux messages du serveur, implémentées par les controllers de jeu **/
public interface GameClientListener{
    void onRoomWaiting(int currentPlayerNumber, int maxPlayersNumber);

    void onRoomFull();

    void onCurrentPlayer(String playerName, boolean isMyTurn);

    void onBoardCreation(int boardSize);

    void onMovePlayed(Pawn pawn, int h, int w, int boardSize);

    /** winner vaut null en cas d'égalité **/
    void onGameOver(String winner, boolean isWinner);
}
