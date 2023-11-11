package com.hivemind.controllers;

import com.hivemind.DatabaseConn;
import com.hivemind.SceneController;
import com.hivemind.SocketConn;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;

public class PairController {

    @FXML
    TextField parent, child, ip, port, timer;

    @FXML
    CheckBox status;

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

    public void pairSubmit() throws IOException, SQLException {

        // TEMP STUFF
        SocketConn conn = new SocketConn(parent.getText(), child.getText(), InetAddress.getByName(ip.getText()), InetAddress.getByName(ip.getText()), Integer.parseInt(port.getText()), status.selectedProperty().get(), Integer.parseInt(timer.getText()));
        DatabaseConn db = new DatabaseConn();

        db.saveNewSocket(parent.getText(), child.getText(), ip.getText(), ip.getText(), Integer.parseInt(port.getText()), status.selectedProperty().get(), Integer.parseInt(timer.getText()));
        db.loadExistingSockets();

        //File file = new File("C:\\Users\\gage1\\Documents\\GitHub\\JAVA3033-Hivemind\\README.md");
        //conn.sendFile(file);

    }

}
