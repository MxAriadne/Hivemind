package com.hivemind.controllers;

import com.hivemind.DatabaseConn;
import com.hivemind.SceneController;
import com.hivemind.SocketConn;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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
import java.net.InetAddress;
import java.net.UnknownHostException;
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
		sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		sp.setMaxHeight(Double.MAX_VALUE);

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
	 * It then creates SocketConn objects for each socket.
	 * This starts the file syncing process.
	 *
	 */
	public static void loadSockets(ScrollPane sp) {
		// Define VBox for connections in the Scrollpane
		VBox content = new VBox();
		content.setStyle("-fx-background-color : #6E476A;");
		content.setSpacing(10);
		content.prefWidthProperty().bind(sp.widthProperty());
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
				Button btn = new Button("Directory: " + dir + "\n" +
						                   "IP: " + clientIP + "\t\tPort: " + port);
				btn.setTextFill(Paint.valueOf("dfbb0a"));
				btn.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
				btn.setTextAlignment(TextAlignment.CENTER);
				btn.setFont(Font.font(14));
				btn.setMaxWidth(Double.MAX_VALUE);

				btn.setOnAction(e -> {
					try {
						db.deleteSocket(dir, clientIP, socketPort, timer);
						loadSockets(sp);
					} catch (SQLException ex) {
						System.out.println(FAILURE + "Failed to delete socket!");
					}
				});

				content.getChildren().add(btn);
			}
			sp.setStyle("-fx-background-color: black;");
			sp.setContent(content);
		} catch (SQLException e) {
			// Only triggers if the ResultSet is null.
			System.out.println(FAILURE + "Database is empty or inaccessible!");
		}
	}

}
