package com.hivemind.controllers;

import com.hivemind.Main;
import com.hivemind.SocketConn;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PairController {

    @FXML
    TextField parent, child, ip, port, timer;

    @FXML
    CheckBox status;

    @FXML
    protected void initialize() {

    }

    public void pairSubmit() throws IOException {
        SocketConn conn = new SocketConn(parent.getText(), child.getText(), InetAddress.getByName(ip.getText()), InetAddress.getByName(ip.getText()), Integer.parseInt(port.getText()), status.selectedProperty().get(), Integer.parseInt(timer.getText()));

        File file = new File("C:\\Users\\gage1\\Documents\\GitHub\\JAVA3033-Hivemind\\README.md");
        conn.sendFile(file);

    }

}
