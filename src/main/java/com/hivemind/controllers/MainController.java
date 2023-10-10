package com.hivemind.controllers;

import com.hivemind.Main;
import com.hivemind.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;



import java.io.IOException;

//TODO
public class MainController {

    @FXML
    Button pair_pc;

    @FXML
    Button pair_cloud;
    
    @FXML
    Pane pane;

    @FXML
    protected void initialize() {

    }

    @FXML
    private void handleButtonClick(ActionEvent e) throws IOException {
        Stage stage = (Stage) ((Node) e.getTarget()).getScene().getWindow();
        SceneController sceneController = new SceneController();
        Button clickedButton = (Button) e.getSource();

        if (clickedButton == pair_pc) {
            sceneController.setView(stage, "pair.fxml");
        }
        if (clickedButton == pair_cloud) {
            sceneController.setView(stage, "pairCloud.fxml");
        }
    }
}