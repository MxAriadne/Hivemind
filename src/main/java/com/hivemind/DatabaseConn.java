package com.hivemind;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;

public class DatabaseConn {

	//Console themeing for easy diag
	public static final String SUCCESS = "\033[1;92m" + "SUCCESS: " + "\033[1;90m";
	public static final String FAILURE = "\033[1;91m" + "FAILURE: " + "\033[1;90m";

	public DatabaseConn() {
		//db instatiation
		Connection connection = null;
		BufferedReader reader = null;
		Statement statement = null;

		try {
			// attempts db connection
			String databaseURL = "jdbc:sqlite:test.db";
			connection = DriverManager.getConnection(databaseURL, "", "");

			// if conn is valid, print
			if (connection.isValid(1)) {
				System.out.println(SUCCESS + "Connected to the SQLite database.");
			} else {
				System.out.println(FAILURE + "Could not connect to the SQLite database.");
			}

			// this runs the init file of commands
			reader = new BufferedReader(new FileReader("commands.sql"));
			connection = DriverManager.getConnection(databaseURL);
			statement = connection.createStatement();

			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(SUCCESS + line);
				statement.execute(line);
			}
		} catch (Exception e) {
			System.out.println(FAILURE + e);
		} finally {
			try {
				if (reader != null) reader.close();
				if (statement != null) statement.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				System.out.println(FAILURE + e);
			}
		}
	}

	public boolean saveNewSocket(String dir, String clientIP, int socketPort, int timer) throws SQLException {

		// attempts db connection
		// not wrapping in a try/catch because the program would not run if this connection was not valid
		// so we can only get to this point when the db is valid
		String databaseURL = "jdbc:sqlite:test.db";
		Connection connection = DriverManager.getConnection(databaseURL, "", "");
		Statement statement = connection.createStatement();

		// query to run
		String query = String.format("INSERT INTO connection(dir, clientIP, socketPort, timer) VALUES (\"%s\",\"%s\",\"%s\",\"%s\");", dir, clientIP, socketPort, timer);

		try {
			//returns the outcome
			return statement.execute(query);
		} catch (Exception e) {
			System.out.println(FAILURE + e);
			return false;
		}

	}

	// pulls socket info and loads into memory
	public ResultSet loadExistingSockets() throws SQLException {

		// attempts db connection
		// not wrapping in a try/catch because the program would not run if this connection was not valid
		// so we can only get to this point when the db is valid
		String databaseURL = "jdbc:sqlite:test.db";
		Connection connection = DriverManager.getConnection(databaseURL, "", "");
		Statement statement = connection.createStatement();

		// Select everything
		String query = "SELECT * FROM connection";

		// This returns a ResultSetMetaData object that contains all the records
		return statement.executeQuery(query);

	}

	// update socketconn
	public boolean updateSocket(String variable, String value, int id) throws SQLException {
		// attempts db connection
		// not wrapping in a try/catch because the program would not run if this connection was not valid
		// so we can only get to this point when the db is valid
		String databaseURL = "jdbc:sqlite:test.db";
		Connection connection = DriverManager.getConnection(databaseURL, "", "");
		Statement statement = connection.createStatement();

		// query to run
		String query = String.format("UPDATE connection SET \"%s\" = \"%s\" WHERE id EQUALS \"%s\";", variable, value, id);

		try {
			//returns the outcome
			return statement.execute(query);
		} catch (Exception e) {
			System.out.println(FAILURE + e);
			return false;
		}
	}

	/*// update watchservice
	// this needs to save
	public boolean updateWatch() {

	}

	// load watchservice in order to compare
	// idk if this needs to return watchservice lol this is a placeholder
	public WatchService loadWatch() {

	}*/

}
