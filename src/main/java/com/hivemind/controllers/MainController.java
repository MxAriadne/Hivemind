package com.hivemind.controllers;

import com.hivemind.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.io.IOException;

//TODO
public class MainController {

    @FXML
    Button pair_pc, hide_window;


    @FXML
    protected void initialize() {
        applyFadeTransition(pair_pc);
        applyFadeTransition(hide_window);
    }

    private void applyFadeTransition(Button button) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), button);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.5);
        fadeTransition.play();
    }


    @FXML
    private void handleButtonClick(ActionEvent e) throws IOException {
        Stage stage = (Stage) ((Node) e.getTarget()).getScene().getWindow();
        SceneController sceneController = new SceneController();
        Button clickedButton = (Button) e.getSource();

        if (clickedButton == pair_pc) {
            sceneController.setView(stage, "pair.fxml");
        } else if (clickedButton == hide_window) {
            stage.setIconified(true);
        }
    }
}