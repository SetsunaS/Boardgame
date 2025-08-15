module com.example.boardgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens setsuna.boardgame to javafx.fxml;
    opens setsuna.boardgame.controller to javafx.fxml;
    opens setsuna.boardgame.model.general to javafx.fxml;
    opens setsuna.boardgame.model.general.exception to javafx.fxml;
    opens setsuna.boardgame.utils to javafx.fxml;

    exports setsuna.boardgame;
    exports setsuna.boardgame.controller;
    exports setsuna.boardgame.model.general;
    exports setsuna.boardgame.model.general.exception;
    exports setsuna.boardgame.utils;
}