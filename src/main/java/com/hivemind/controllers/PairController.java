package com.hivemind.controllers;

import com.hivemind.DatabaseConn;
import com.hivemind.SceneController;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;

public class PairController {

    @FXML
    TextField dir, clientIP, port, timer;

    @FXML
    Button cancel;

    @FXML
    Text textPair;

    @FXML
    protected void initialize() {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), textPair);
        fadeTransition.setFromValue(0.0); // Start fully transparent
        fadeTransition.setToValue(1.0);   // Fade to fully opaque
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
        DatabaseConn db = new DatabaseConn();
        db.saveNewSocket(dir.getText(), clientIP.getText(), Integer.parseInt(port.getText()), Integer.parseInt(timer.getText()));
        com.hivemind.Main.loadSockets();
        System.out.println("Paired!");
    }

}
