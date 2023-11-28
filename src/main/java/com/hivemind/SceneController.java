package com.hivemind;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneController {

    private Scene scene;
    private Parent content;

    public void setView(Stage stage, String s) throws IOException {
        content = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(s)));
        scene = new Scene(content);
        stage.setScene(scene);
        stage.setTitle("Hivemind");
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

}