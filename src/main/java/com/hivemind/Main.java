package com.hivemind;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    //Console themeing for easy diag
    public static final String GRAY = "\033[1;90m";
    public static final String RED = "\033[1;91m";
    public static final String GREEN = "\033[1;92m";
    public static final String SUCCESS = GREEN + "SUCCESS: " + GRAY;
    public static final String FAILURE = RED + "FAILURE: " + GRAY;

    @Override
    public void start(Stage stage) throws IOException {
        // SceneController obj just has shorthand code for swapping windows and settings parameters for them.
        SceneController sceneController = new SceneController();
        // Start the opening animation
        sceneController.setView(stage, "openScreen.fxml");

        // Wait for 3 seconds and then switch to main.fxml
        // Timeline provides the delay then keyframe specifies the action to be taken after that delay.
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            try {
                sceneController.setView(stage, "main.fxml");
            } catch (IOException e) {
                System.out.println("Something is very wrong if this error triggers.\nLikely missing files.");
            }
        }));

        timeline.play();
        loadSockets();
    }

    public static void loadSockets() {
        DatabaseConn db = new DatabaseConn();
        try {
            ResultSet rs = db.loadExistingSockets();
            while (rs.next()) {
                String dir = rs.getString("dir");
                String clientIP = rs.getString("clientIP");
                int port = rs.getInt("socketPort");
                int timer = rs.getInt("timer");

                new SocketConn(port, timer, dir, InetAddress.getByName(clientIP));
            }
        } catch (SQLException | UnknownHostException e) {
            System.out.println(FAILURE + "Database is empty or inaccessible!");
		}
	}

}