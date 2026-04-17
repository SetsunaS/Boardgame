package setsuna.boardgame.controller;

import setsuna.boardgame.model.general.player.Player;
import java.util.List;

public interface IGameController{
    void createOfflineGameInterface(List<Player> players, int size);

    void createOnlineGameInterface(int roomId);
}