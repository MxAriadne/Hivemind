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
 * SocketConn.java
 * This is the primary class for the program. It starts the socket and WatchService for every paired connection.
 *
 */

package com.hivemind;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

public class SocketConn extends Main {
    private final InetAddress clientIP;
    // Socket for the incoming connection
    // Port for the transfer
    private final int port;
    // Directory that will be synced
    private final String directoryPath;
    // Amount of time between directory checks
    int timer;
    // Number of directories being watched. This is just for debugging.
    int count = 0;
    // Server socket
    private ServerSocket serverSocket;
    // Client IP address for authentication
    private Socket clientSocket;
    // WatchService that triggers on changes to the directory
    private WatchService watchService;
    // Authenticates subdirectories then adds them to the WatchService if valid
    private Map<WatchKey, Path> keyPathMap = new HashMap<>();

    //Console themeing for easy diag
    public static final String GRAY = "\033[1;90m";
    public static final String RED = "\033[1;91m";
    public static final String GREEN = "\033[1;92m";
    public static final String SUCCESS = GREEN + "SUCCESS: " + GRAY;
    public static final String FAILURE = RED + "FAILURE: " + GRAY;

    /*
     * SocketConn
     *
     * Constructor
     * Initialize the ip, port, directory, and timer.
     * Starts a new thread for the socket and starts the watch service via the registerWatchService() function.
     *
     */
    public SocketConn(int socketPort, int watchTimer, String socketDir, InetAddress ip) {
        this.clientIP = ip;
        this.directoryPath = socketDir;
        this.port = socketPort;
        this.timer = watchTimer;

        new Thread(() -> {
            try {
                // Prints debugging information.
                System.out.println(GREEN + "-------------------------------------");
                System.out.println(GRAY + "Attempting to start socket:" + "\n" +
                                          "IP: " + clientIP + "\n" +
                                          "Port: " + port + "\n" +
                                          "Directory: " + directoryPath);
                System.out.println(GREEN + "-------------------------------------");
                // Establish a connection for the given port.
                serverSocket = new ServerSocket(port);
                // Register the directory with the WatchService
                registerWatchService(Paths.get(directoryPath));
            } catch (IOException e) {
                System.out.println(FAILURE + "Port or directory is invalid!");
            }
        // Launch the thread.
        }).start();
    }

    /*
     * registerAll
     *
     * start                [Path]                The path to the directory that'll be synced.
     *
     * This function recursively adds folders to the WatchService.
     * It's called everytime a new directory is created as well.
     *
     */
    private void registerAll(final Path start) throws IOException {
        // Uses walkFileTree to traverse the directory
        Files.walkFileTree(start, new SimpleFileVisitor<>() {
            // Overrides preVisitDirectory in order to call our custom register function for
            // each path discovered.
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                // Adds the directory to the WatchService.
				register(dir);
                // Increases the count of directories in the WatchService.
                count++;
                // Proceed to next dir
				return FileVisitResult.CONTINUE;
			}
		});
        System.out.println(SUCCESS + count + " directories have been registered with the watch service!");
    }

    /*
     * register
     *
     * dir              [Path]                The directory to be added to the WatchService.
     *
     * Registers the path with the WatchService for modification, creation, and deletion events.
     * It then adds the path to the keyPathMap HashMap so that we don't traverse paths we've already been to.
     *
     */
    private void register(Path dir) throws IOException {
        // Register the directory to fire events for modification, creation, and deletion of items.
        WatchKey key = dir.register(watchService, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);
        // Save this to the keypath to track.
        keyPathMap.put(key, dir);
    }

    /*
     * registerWatchService
     *
     * dir              [Path]              The path to the directory that'll be synced.
     *
     * Creates the WatchService for the directory and triggers the send methods on WatchService events.
     *
     */
    private void registerWatchService(Path dir) throws IOException {
        // Initialize the WatchService
        this.watchService = FileSystems.getDefault().newWatchService();
        // Recursively register the subfolders
        registerAll(dir);
        // Create the thread for reading WatchService events.
        new Thread(() -> {
            try {
                // Constant while loop
                while (true) {
                    // Read the WatchService
                    WatchKey key = watchService.take();
                    // Parse through the events
                    for (WatchEvent<?> event : key.pollEvents()) {
                        // If something had been modified or created in the directory...
                        if (event.kind() == ENTRY_MODIFY || event.kind() == ENTRY_CREATE) {
                            // Get the name of the modified item.
                            Path filename = (Path) event.context();
                            // Get the path of the modified item.
                            Path filePath = dir.resolve(filename);
                            // If the item is a directory...
                            if (Files.isDirectory(filePath)) {
                                // Register the new directory with the WatchService
                                registerAll(filePath);
                                System.out.println(SUCCESS + " Registered " + filePath + "with the WatchService!");

                                // Wait for the client to attempt a connection.
                                clientSocket = serverSocket.accept();
                                // Verify that the client is the one we've paired with.
                                if (clientIP.equals(clientSocket.getInetAddress())) {
                                    // If so, send the information.
                                    sendDirectoryCreateRequest(filename);
                                } else {
                                    // If not, error!
                                    System.out.println(FAILURE + "Non-paired computer attempted connection!");
                                }
                            // If the item is actually a file...
                            } else {
                                // Verify that the client is the one we've paired with.
                                if (clientIP.equals(clientSocket.getInetAddress())) {
                                    // If so, send the information.
                                    sendFile(filePath);
                                } else {
                                    // If not, error!
                                    System.out.println(FAILURE + "Non-paired computer attempted connection!");
                                }
                            }

                        }
                        // If a file was deleted...
                        if (event.kind() == ENTRY_DELETE) {
                            // Get the name of the modified item.
                            Path filename = (Path) event.context();
                            // Get the path of the modified item.
                            Path filePath = dir.resolve(filename);
                            // Wait for the client to attempt a connection.
                            clientSocket = serverSocket.accept();
                            // Send the client the deletion request.
                            sendDeleteRequest(filePath);
                        }
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        keyPathMap.remove(key);
                        if (keyPathMap.isEmpty()) {
                            break;
                        }
                    }
                }
            } catch (InterruptedException | IOException e) {
                System.out.println(FAILURE + "Either the thread closed early or the WatchService events resulted in an error!");
            }
        // Start thread
		}).start();
    }

    /*
     * sendFile
     * fullFilePath             [Path]                Path to the file that will be created.
     *
     * The client reads three kinds of event flags.
     * Flag '0' means creation event.
     * Flag '1' means deletion event.
     * Flag '2' means directory creation event.
     *
     * This function sends the creation flag as well as the name of the item to be created.
     *
     */
    private void sendFile(Path fullFilePath) throws IOException {
        // File instatiation
        File file = new File(String.valueOf(fullFilePath));
        // Filename to be sent to client
        String filename = file.getName();
        // Connect to the client write sream
        try (InputStream fileInputStream = new FileInputStream(file);
             OutputStream outputStream = clientSocket.getOutputStream()) {

            // Send the file creation flag '0'
            outputStream.write(0);

            // Sending the filename so the client knows how to save the file.
            byte[] fileNameBytes = filename.getBytes(StandardCharsets.UTF_8);
            // Send filename array length.
            outputStream.write(fileNameBytes.length);
            // Send filename array data.
            outputStream.write(fileNameBytes);

            // Send file content
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            System.out.println(SUCCESS + "File \"" + filename + "\" was sent to " + clientIP + "!");
        } finally {
            // Ensure socket closure after sending file
            clientSocket.close();
        }
    }

    /*
     * sendDeleteRequest
     * fullFilePath             [Path]                Path to the file that will be deleted.
     *
     * The client reads three kinds of event flags.
     * Flag '0' means creation event.
     * Flag '1' means deletion event.
     * Flag '2' means directory creation event.
     *
     * This function sends the deletion flag as well as the name of the item to be deleted.
     *
     */
    private void sendDeleteRequest(Path fullFilePath) throws IOException {
        // File instatiation
        File file = new File(String.valueOf(fullFilePath));
        String filename = file.getName();

        try {
            OutputStream outputStream = clientSocket.getOutputStream();

            // Send the deletion flag
            outputStream.write(1);

            // Sending the filename so the client knows how to save the file.
            byte[] fileNameBytes = filename.getBytes(StandardCharsets.UTF_8);
            // Send filename array length.
            outputStream.write(fileNameBytes.length);
            // Send filename array data.
            outputStream.write(fileNameBytes);

            System.out.println(SUCCESS + "File deletion request was sent for \"" + filename + "\" to the client! IP: " + clientIP);
        } finally {
            // Ensure socket closure after sending file
            clientSocket.close();
        }
    }

    /*
     * sendDirectoryCreateRequest
     * fullFilePath             [Path]                Path to the directory to be created.
     *
     * The client reads three kinds of event flags.
     * Flag '0' means creation event.
     * Flag '1' means deletion event.
     * Flag '2' means directory creation event.
     *
     * This function sends the directory creation flag as well as the name of the directory to be created.
     *
     */
    private void sendDirectoryCreateRequest(Path fullFilePath) throws IOException {
        try (OutputStream outputStream = clientSocket.getOutputStream()) {
            // Send the directory creation flag
            outputStream.write(2);

            // Sending the directory name so the client knows how to save the file.
            byte[] directoryPathBytes = fullFilePath.toString().getBytes(StandardCharsets.UTF_8);
            // Send directory name array length.
            outputStream.write(directoryPathBytes.length);
            // Send directory name array data.
            outputStream.write(directoryPathBytes);

            System.out.println(SUCCESS + "Directory \"" + fullFilePath.getFileName() + "\" was sent to " + clientIP + "!");
        } catch (IOException e) {
            System.out.println(FAILURE + "Directory \"" + fullFilePath.getFileName() + "\" failed to send!");
        } finally {
            // Ensure socket closure after sending file
            clientSocket.close();
        }
    }
}
