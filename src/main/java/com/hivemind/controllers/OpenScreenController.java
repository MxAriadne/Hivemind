package com.hivemind.controllers;

import javafx.fxml.FXML;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class OpenScreenController {
    @FXML
    ImageView logo;

    @FXML
    Text hiveMindLogo;

    @FXML
    Text welcome; //was the welcome text now file sync

    @FXML
    protected void initialize() {
        welcome.setVisible(false);  //not visible
        hiveMindLogo.setVisible(false); //not visible
        // Slide the logo to the left over 1.7 seconds
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2.1), logo);
        translateTransition.setToX(-180); // Slide to the left by -180 pixels from center
        translateTransition.play(); //play animation


        // Fade in the HiveMind text over 2.1 seconds
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2.4), hiveMindLogo);
        fadeTransition.setFromValue(0); // Start with opacity 0
        hiveMindLogo.setVisible(true); // visible
        fadeTransition.setToValue(1); // End with opacity 1
        fadeTransition.play();

        // Set onFinished event for when the logo event stops
        fadeTransition.setOnFinished(event -> {
            // After logo animation is finished, show the Welcome text
            welcome.setVisible(true);
        });
    }
}
