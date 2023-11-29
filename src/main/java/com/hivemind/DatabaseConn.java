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
 * DatabaseConn.java
 * This class hosts several methods for interacting with the SQLite database.
 *
 */

package com.hivemind;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;

public class DatabaseConn {

	//Console themeing for easy diag
	public static final String SUCCESS = "\033[1;92m" + "SUCCESS: " + "\033[1;90m";
	public static final String FAILURE = "\033[1;91m" + "FAILURE: " + "\033[1;90m";

	/*
	 * DatabaseConn
	 *
	 * Constructor
	 * Initialize database if it isn't already.
	 *
	 */
	public DatabaseConn() {
		//Holder instatiation
		Connection connection = null;
		BufferedReader reader = null;
		Statement statement = null;

		try {
			// Attempt database connection.
			String databaseURL = "jdbc:sqlite:test1.db";
			// This is a local integrated database so it doesn't require a user and password.
			connection = DriverManager.getConnection(databaseURL, "", "");

			// Validate the database connection.
			if (connection.isValid(1)) {
				System.out.println(SUCCESS + "Connected to the SQLite database.");
			} else {
				System.out.println(FAILURE + "Could not connect to the SQLite database.");
			}

			// Runs the schema initialization statements.
			reader = new BufferedReader(new FileReader("commands.sql"));
			connection = DriverManager.getConnection(databaseURL);
			statement = connection.createStatement();

			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(SUCCESS + line);
				statement.execute(line);
			}
		} catch (Exception e) {
			System.out.println(FAILURE + "Unable to run schema statements!");
		} finally {
			try {
				if (reader != null) reader.close();
				if (statement != null) statement.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				System.out.println(FAILURE + "Failed to close database connection!");
			}
		}
	}

	/*
	 * saveNewSocket
	 *
	 * dir              	  [String]                The path to the directory for the socket.
	 * clientIP               [String]                The client IP address for the socket.
	 * socketPort             [int]                	  The port used for this socket.
	 * timer                  [int]                   The timer used for this socket.
	 *
	 * This functions saves socket data to the database.
	 *
	 */
	public void saveNewSocket(String dir, String clientIP, int socketPort, int timer) throws SQLException {

		// Attempt DB connection.
		// Not wrapping in a try/catch because the program would not run if this connection was not valid.
		String databaseURL = "jdbc:sqlite:test1.db";
		Connection connection = DriverManager.getConnection(databaseURL, "", "");
		Statement statement = connection.createStatement();

		// Statement sent to the DB
		String query = String.format("INSERT INTO connection(dir, clientIP, socketPort, timer) VALUES (\"%s\",\"%s\",\"%s\",\"%s\");", dir, clientIP, socketPort, timer);

		try {
			// Attempt to send statement.
			statement.execute(query);
		} catch (Exception e) {
			System.out.println(FAILURE + "Unable to add socket data to the database!");
		}

	}

	/*
	 * loadExistingSockets
	 *
	 * Returns a ResultSet containing all the data within the database.
	 * This is used in Main.java in order to instantiate every socket saved in the database on init.
	 *
	 */
	public ResultSet loadExistingSockets() throws SQLException {

		// Attempt DB connection.
		// Not wrapping in a try/catch because the program would not run if this connection was not valid.
		String databaseURL = "jdbc:sqlite:test1.db";
		Connection connection = DriverManager.getConnection(databaseURL, "", "");
		Statement statement = connection.createStatement();

		// Select everything in the DB
		String query = "SELECT * FROM connection";

		// This returns a ResultSetMetaData object that contains all the records
		return statement.executeQuery(query);

	}

	/*
	 * deleteSocket
	 *
	 * dir              	  [String]                The path to the directory for the socket.
	 * clientIP               [String]                The client IP address for the socket.
	 * socketPort             [int]                	  The port used for this socket.
	 * timer                  [int]                   The timer used for this socket.
	 *
	 * This function deletes a socket from the database.
	 *
	 */
	public void deleteSocket(String dir, String clientIP, String socketPort, String timer) throws SQLException {

		// Attempt DB connection.
		// Not wrapping in a try/catch because the program would not run if this connection was not valid.
		String databaseURL = "jdbc:sqlite:test1.db";
		Connection connection = DriverManager.getConnection(databaseURL, "", "");
		Statement statement = connection.createStatement();

		// Statement sent to the DB
		String query = String.format("DELETE FROM connection WHERE dir=\"%s\" AND clientIP=\"%s\" AND socketPort=%s AND timer=%s;", dir, clientIP, socketPort, timer);

		try {
			// Attempt to send statement.
			statement.execute(query);
		} catch (Exception e) {
			System.out.println(FAILURE + "Unable to remove socket data from the database!");
			System.out.println(FAILURE + e);
		}
	}
}
