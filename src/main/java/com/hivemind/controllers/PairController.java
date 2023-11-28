package com.hivemind.controllers;

import com.hivemind.DatabaseConn;
import com.hivemind.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class PairController {

    @FXML
    TextField dir, clientIP, port, timer;

    @FXML
    Button cancel;

    @FXML
    protected void initialize() {

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
