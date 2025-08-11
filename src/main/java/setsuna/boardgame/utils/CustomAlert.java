package setsuna.boardgame.utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class CustomAlert{
    @FXML
    private Label contentText;

    @FXML
    private Button yesButton;

    @FXML
    private Button noButton;

    private boolean result=false;


    public void setText(String text){
        contentText.setText(text);
    }

    @FXML
    private void clickOnYes(ActionEvent actionEvent){
        result=true;
        closeStage();
    }

    @FXML
    private void clickOnNo(ActionEvent actionEvent){
        result=false;
        closeStage();
    }

    public boolean getResult(){
        return result;
    }

    private void closeStage(){
        Stage stage=(Stage)yesButton.getScene().getWindow();
        stage.close();
    }
}