/*
 * Group 2
 * Kevin Kongmanychanh
 * Andrew Chayavon
 * Kennedy Bowles
 * Christian Mertz
 *
 * CSCI 3033
 * Dr. Al-Tobasei
 * 11/30/2023
 *
 * MainController.java
 * Controller for main.fxml
 *
 */

package com.hivemind.controllers;

import com.hivemind.SceneController;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MainController {

    //Console themeing for easy diag
    public static final String GRAY = "\033[1;90m";
    public static final String RED = "\033[1;91m";
    public static final String GREEN = "\033[1;92m";
    public static final String SUCCESS = GREEN + "SUCCESS: " + GRAY;
    public static final String FAILURE = RED + "FAILURE: " + GRAY;

    @FXML
    Button pair_pc, hide_window, listBtn;

    @FXML
    protected void initialize() {
        // Init SceneController object
        SceneController sc = new SceneController();
        // Add action to hide_window button that obviously hides the window when clicked.
        hide_window.setOnAction(e -> hide_window.getScene().getWindow().hide());
        // Adds animations to the buttons.
        applyFadeTransition(pair_pc);
        applyFadeTransition(hide_window);
        applyFadeTransition(listBtn);
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
        } else if (clickedButton == listBtn) {
            sceneController.setView(stage, "list.fxml");
        }
    }
}