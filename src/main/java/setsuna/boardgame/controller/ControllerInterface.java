package setsuna.boardgame.controller;

import setsuna.boardgame.utils.network.NetworkManager;
import setsuna.boardgame.model.general.player.Player;

public interface ControllerInterface{
    void setCurrentPlayer(Player player);

    void setNetworkManager(NetworkManager networkManager);
}