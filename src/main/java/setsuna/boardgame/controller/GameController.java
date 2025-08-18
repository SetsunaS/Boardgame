package setsuna.boardgame.controller;

import setsuna.boardgame.controller.network.NetworkManager;
import setsuna.boardgame.model.general.player.Player;

public interface GameController{
    void setCurrentPlayer(Player player);

    void setNetworkManager(NetworkManager networkManager);
}