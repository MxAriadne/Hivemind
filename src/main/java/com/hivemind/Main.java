/*
 * Group 2
 * Freyja Richardson
 * Kevin Kongmanychanh
 * Andrew Chayavon
 * Kennedy Bowles
 * Christian Mertz
 *
 * CSCI 3033
 * Dr. Al-Tobasei
 * 11/30/2023
 *
 * Main.java
 * This is the main class for the program. It uses SceneController to start the opening animation and swaps to MainController afterwards.
 * It also implements the function necessary for the System Tray icon and initializes all of the paired sockets on boot.
 *
 */

package com.hivemind;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main extends Application {

    //launch
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
                // Switch to the main menu
                sceneController.setView(stage, "main.fxml");
                // Disable implicit exit, this makes it so the main thread stays open when stage.hide() is used.
                Platform.setImplicitExit(false);
                // Creates the tray icon.
                createTrayIcon(stage);
            } catch (IOException e) {
                // This will only trigger if there is a compilation error in MainController or if the files are missing.
                System.out.println(FAILURE + "Something is very wrong if this error triggers. Likely missing files.");
            }
        }));

        // Start the animation.
        timeline.play();
        // Once the main menu is open, load the saved sockets from the database.
        loadSockets();
    }

    /*
     * loadSockets()
     *
     * This method takes no input parameters.
     * Loads the paired sockets from the database into memory.
     * It then creates SocketConn objects for each socket.
     * This starts the file syncing process.
     *
     */
    public static void loadSockets() {
        // Initialize database helper file.
        DatabaseConn db = new DatabaseConn();
        // Attempt to read database
        try {
            // Pulls the results of "SELECT * FROM connections"
            // which just gives every socket.
            ResultSet rs = db.loadExistingSockets();
            // While there are still rows...
            while (rs.next()) {
                // Directory to be synced
                String dir = rs.getString("dir");
                // IP address of the client
                String clientIP = rs.getString("clientIP");
                // Open port for connection
                int port = rs.getInt("socketPort");
                // Time between directory checks
                int timer = rs.getInt("timer");
                // No need to save the SocketConn object as a variable since the constructor
                // starts the WatchService thread.
                new SocketConn(port, timer, dir, InetAddress.getByName(clientIP));
            }
        } catch (SQLException | UnknownHostException e) {
            // Only triggers if the ResultSet is null.
            System.out.println(FAILURE + "Database is empty or inaccessible!");
		}
	}

    /*
     * createTrayIcon()
     *
     * stage                Stage               The window for the main program.
     *
     * This function creates the tray icon for the program and creates the listener
     * event to reopen the program when the clay icon is clicked.
     *
     */
    public void createTrayIcon(final Stage stage) throws IOException {
        // TrayIcon self-explanatory, this is the object that displays in the system tray on launch.
        TrayIcon trayIcon;
        // Check if this is a Windows PC, if not just minimize to taskbar like normal.
        if (SystemTray.isSupported()) {
            // Get the SystemTray instance.
            SystemTray tray = SystemTray.getSystemTray();
            // Get Hivemind logo for the tray icon.
            Image image = ImageIO.read(getClass().getResource("hivemindTray.png"));

            // Establish ActionListener for when the tray icon is clicked on
            ActionListener show = e -> Platform.runLater(() -> {
                // Check stage is still valid, start() and initialize() for controllers can cause stage to be null in special cases.
                // This stops the program from hanging.
                if (stage != null) {
                    // Show stage
                    stage.show();
                } else {
                    System.out.println(FAILURE + "Stage is null!");
                }
            });

            // Initialize tray icon.
            trayIcon = new TrayIcon(image, "Hivemind - Server");
            // Add the listener
            trayIcon.addActionListener(show);

            try {
                // Attempt to add to the system tray.
                tray.add(trayIcon);
                System.out.println(SUCCESS + "Tray icon added!");
            } catch (AWTException e) {
                System.out.println(FAILURE + "Exception occured! Unable to add tray icon.");
            }
        } else {
            System.out.println("System tray is not supported on this platform.");
        }
    }
}