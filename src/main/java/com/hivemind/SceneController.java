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
 * SceneController.java
 * This is helper class that changes the scene while maintaining all the same parameters
 * as the other scenes.
 *
 */

package com.hivemind;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneController {

    public void setView(Stage stage, String s) throws IOException {
        // Load the FXML file
        Parent content = FXMLLoader.load(getClass().getResource(s));
        // Initialize a new scene object with the FXML file
        Scene scene = new Scene(content);
        // Add the scene to the main stage
        stage.setScene(scene);
        // Since this program relies on threads we close it using System.exit
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        // Set the taskbar and window icon to the logo.
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("hivemind.png")));
        // Set the title.
        stage.setTitle("Hivemind - Server");
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

}