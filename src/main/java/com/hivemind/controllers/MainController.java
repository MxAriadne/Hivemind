package com.hivemind.controllers;

import com.hivemind.Main;
import com.hivemind.SceneController;
import com.hivemind.SocketConn;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;

//TODO
public class MainController {

    @FXML
    Button pair_pc, pair_cloud, test1, test2;
    
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
        if (clickedButton == test1) {
            //todo: this is temporary to test connection def needs removed/replaced for final
            InetAddress serverAddress = InetAddress.getLocalHost();
            int serverPort = 9001;
            //ServerSocket serverSocket = new ServerSocket(serverPort, Integer.MAX_VALUE, serverAddress);
            SocketConn serverSocket = new SocketConn("C:\\", "C:\\", InetAddress.getLocalHost(), InetAddress.getLocalHost(), 80, true, Integer.MAX_VALUE);

            Thread incoming = new Thread(() -> {
                try {
                    serverSocket.sendFile();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            incoming.start();

        }
        if (clickedButton == test2) {

        }
    }
}