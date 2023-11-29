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
 * PairController.java
 * Controller for pair.fxml
 *
 */

package com.hivemind.controllers;

import com.hivemind.DatabaseConn;
import com.hivemind.SceneController;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;

public class PairController {

    //Console themeing for easy diag
    public static final String GRAY = "\033[1;90m";
    public static final String RED = "\033[1;91m";
    public static final String GREEN = "\033[1;92m";
    public static final String SUCCESS = GREEN + "SUCCESS: " + GRAY;
    public static final String FAILURE = RED + "FAILURE: " + GRAY;

    @FXML
    TextField dir, clientIP, port, timer;

    @FXML
    Button cancel;

    @FXML
    Text textPair;

    @FXML
    protected void initialize() {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), textPair);
        // Start fully transparent
        fadeTransition.setFromValue(0.0);
        // Fade to fully opaque
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    @FXML
    private void handleButtonClick(ActionEvent e) throws IOException {
        Stage stage = (Stage) ((Node) e.getTarget()).getScene().getWindow();
        SceneController sceneController = new SceneController();
        Button clickedButton = (Button) e.getSource();

        if (clickedButton == cancel) {
            sceneController.setView(stage, "main.fxml");
        }
    }

    public void pairSubmit() throws SQLException {
        // Init DB object
        DatabaseConn db = new DatabaseConn();
        // Send TextField data to the database.
        db.saveNewSocket(dir.getText(), clientIP.getText(), Integer.parseInt(port.getText()), Integer.parseInt(timer.getText()));
        // Reload all the sockets.
        com.hivemind.Main.loadSockets();
        System.out.println(SUCCESS + "Successfully paired!");
    }

}
