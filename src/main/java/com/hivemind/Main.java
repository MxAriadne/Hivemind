package com.hivemind;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;


import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        //start with openScreen scene
        FXMLLoader openScreenLoader = new FXMLLoader(Main.class.getResource("openScreen.fxml"));
        Scene openScreenScene = new Scene(openScreenLoader.load());
        stage.setScene(openScreenScene);
        stage.setTitle("Hivemind - Open Screen");
        stage.setResizable(false);
        stage.show();

        // Wait for 5 seconds and then switch to main.fxml
        //The Timeline provided the delay, and the KeyFrame specified the action to be taken after the delay
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            try {
                FXMLLoader mainLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
                Scene mainScene = new Scene(mainLoader.load());
                stage.setScene(mainScene);
                stage.setTitle("Hivemind - Main UI");
                stage.show();
            } catch (IOException ignored) {
            }
        }));
        timeline.play();
    }

    public static void main(String[] args) {
        launch();
    }
}