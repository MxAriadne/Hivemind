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
 * OpenScreenController.java
 * Controller for openScreen.fxml
 *
 */

package com.hivemind.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class OpenScreenController {
    @FXML
    ImageView logo;

    @FXML
    Text hiveMindLogo;

    @FXML
    Text welcome; //was the welcome text now file sync

    @FXML
    protected void initialize()  {
        // Start invisible.
        welcome.setVisible(false);
        // Start invisible.
        hiveMindLogo.setVisible(false);
        // Slide the logo to the left over 2.1 seconds
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2.1), logo);
        // Slide to the left by -180 pixels from center
        translateTransition.setToX(-180);
        // Play animation
        translateTransition.play();

        // Fade in the HiveMind text over 2.1 seconds
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2.4), hiveMindLogo);
        // Start with opacity 0
        fadeTransition.setFromValue(0);
        // Make visible now.
        hiveMindLogo.setVisible(true);
        // End with opacity 1
        fadeTransition.setToValue(1);
        fadeTransition.play();

        // Set onFinished event for when the logo event stops
        fadeTransition.setOnFinished(event -> {
            // After logo animation is finished, show the Welcome text
            welcome.setVisible(true);
        });
    }
}
