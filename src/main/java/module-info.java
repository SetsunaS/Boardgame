module setsuna.boardgame{
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;

    opens setsuna.boardgame to javafx.fxml;
    opens setsuna.boardgame.controller to javafx.fxml;
    opens setsuna.boardgame.database to javafx.fxml;
    opens setsuna.boardgame.model.games to javafx.fxml;
    opens setsuna.boardgame.model.general to javafx.fxml;
    opens setsuna.boardgame.model.general.exception to javafx.fxml;
    opens setsuna.boardgame.utils to javafx.fxml;
    opens setsuna.boardgame.utils.password to javafx.fxml;

    exports setsuna.boardgame;
    exports setsuna.boardgame.controller;
    exports setsuna.boardgame.database;
    exports setsuna.boardgame.model.games;
    exports setsuna.boardgame.model.general;
    exports setsuna.boardgame.model.general.exception;
    exports setsuna.boardgame.utils;
    exports setsuna.boardgame.utils.password;
    exports setsuna.boardgame.model.general.player;
    opens setsuna.boardgame.model.general.player to javafx.fxml;
}