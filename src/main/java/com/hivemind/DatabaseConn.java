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
			String databaseURL = "jdbc:sqlite:structure.db";
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

	public boolean saveNewSocket(String parentDir, String childDir, String childIP, String parentIP, int socketPort, boolean status, int timer) throws SQLException {

		// attempts db connection
		// not wrapping in a try/catch because the program would not run if this connection was not valid
		// so we can only get to this point when the db is valid
		String databaseURL = "jdbc:sqlite:structure.db";
		Connection connection = DriverManager.getConnection(databaseURL, "", "");
		Statement statement = connection.createStatement();
		String query = String.format("INSERT INTO connection(parentDir, childDir, childIP, parentIP, socketPort, status, timer) VALUES (\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\");", parentDir, childDir, childIP, parentIP, socketPort, status, timer);

		System.out.println(query);

		try {
			return statement.execute(query);
		} catch (Exception e) {
			System.out.println(FAILURE + e);
			return false;
		}

	}

	// pulls socket info and loads into memory
	public void loadExistingSocket() throws SQLException {

		// attempts db connection
		// not wrapping in a try/catch because the program would not run if this connection was not valid
		// so we can only get to this point when the db is valid
		String databaseURL = "jdbc:sqlite:structure.db";
		Connection connection = DriverManager.getConnection(databaseURL, "", "");
		Statement statement = connection.createStatement();
		//String query = String.format("INSERT INTO connection VALUES (%s, %s, %s, %s, %s, %s, %s);", parentDir, childDir, childIP, parentIP, socketPort, status, timer);
		String query = "SELECT * FROM connection";

		try {
			ResultSet test = statement.executeQuery(query);
			ResultSetMetaData rsmd = test.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			while (test.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					if (i > 1) System.out.print(",  ");
					String columnValue = test.getString(i);
					System.out.print(columnValue + " " + rsmd.getColumnName(i));
				}
				System.out.println("");
			}
		} catch (Exception e) {
			System.out.println(FAILURE + e);
		}

	}

	/*// update socketconn
	public boolean updateSocket() {

	}

	// update watchservice
	// this needs to save
	public boolean updateWatch() {

	}

	// load watchservice in order to compare
	// idk if this needs to return watchservice lol this is a placeholder
	public WatchService loadWatch() {

	}*/

}
