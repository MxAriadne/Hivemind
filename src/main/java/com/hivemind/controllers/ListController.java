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
 * ListController.java
 * Controller for list.fxml
 *
 */

package com.hivemind.controllers;

import com.hivemind.DatabaseConn;
import com.hivemind.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ListController {

	//Console themeing for easy diag
	public static final String GRAY = "\033[1;90m";
	public static final String RED = "\033[1;91m";
	public static final String GREEN = "\033[1;92m";
	public static final String SUCCESS = GREEN + "SUCCESS: " + GRAY;
	public static final String FAILURE = RED + "FAILURE: " + GRAY;

	@FXML
	Button back;

	@FXML
	ScrollPane sp;

	@FXML
	protected void initialize() {
		// Disables the horizontal scrollbar
		sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		// Makes it dynamically grow with the elements added.
		sp.setMaxHeight(Double.MAX_VALUE);
		// This adds the sockets to the list.
		loadSockets(sp);
	}

	@FXML
	private void handleButtonClick(ActionEvent e) throws IOException {
		Stage stage = (Stage) ((Node) e.getTarget()).getScene().getWindow();
		SceneController sceneController = new SceneController();
		Button clickedButton = (Button) e.getSource();

		if (clickedButton == back) {
			sceneController.setView(stage, "main.fxml");
		}
	}

	/*
	 * loadSockets()
	 *
	 * This method takes no input parameters.
	 * Loads the paired sockets from the database into memory.
	 * It then iterates over them and adds them to the ScrollPane for displaying.
	 *
	 */
	public static void loadSockets(ScrollPane sp) {
		// Define VBox for connections in the Scrollpane
		VBox content = new VBox();
		// Set background color
		content.setStyle("-fx-background-color : #6E476A;");
		content.setSpacing(10);
		// Set width to equal the ScrollPane that the Vbox will be inside of.
		content.prefWidthProperty().bind(sp.widthProperty());
		// Set height to equal the ScrollPane that the Vbox will be inside of.
		content.prefHeightProperty().bind(sp.heightProperty());
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
				// Directory to be synced
				String timer = rs.getString("timer");
				// Directory to be synced
				String socketPort = rs.getString("socketPort");
				// IP address of the client
				String clientIP = rs.getString("clientIP");
				// Open port for connection
				int port = rs.getInt("socketPort");
				// Button containing connection info
				Button btn = new Button("Directory: " + dir + "\n" +
						                   "IP: " + clientIP + "\t\tPort: " + port);
				// Set text color
				btn.setTextFill(Paint.valueOf("dfbb0a"));
				// Set background color
				btn.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
				btn.setTextAlignment(TextAlignment.CENTER);
				btn.setFont(Font.font(14));
				// Make the button take up the full width of the menu
				btn.setMaxWidth(Double.MAX_VALUE);
				// Give every button an event handler that deletes the socket.
				btn.setOnAction(e -> {
					try {
						// Delete socket selected
						db.deleteSocket(dir, clientIP, socketPort, timer);
						// Reload
						loadSockets(sp);
					} catch (SQLException ex) {
						System.out.println(FAILURE + "Failed to delete socket!");
					}
				});
				// Add the button to the Vbox.
				content.getChildren().add(btn);
			}
			// Set background color
			sp.setStyle("-fx-background-color: black;");
			// Add Vbox to ScrollPane
			sp.setContent(content);
		} catch (SQLException e) {
			// Only triggers if the ResultSet is null.
			System.out.println(FAILURE + "Database is empty or inaccessible!");
		}
	}
}
