module com.example.boardgame {
    requires javafx.controls;
    requires javafx.fxml;

    opens setsuna.boardgame to javafx.fxml;
    exports setsuna.boardgame;
    exports setsuna.boardgame.controller;
    opens setsuna.boardgame.controller to javafx.fxml;
    exports setsuna.boardgame.model.general;
    opens setsuna.boardgame.model.general to javafx.fxml;
    exports setsuna.boardgame.model.general.exception;
    opens setsuna.boardgame.model.general.exception to javafx.fxml;
}