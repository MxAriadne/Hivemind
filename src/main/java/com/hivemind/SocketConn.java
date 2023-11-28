package com.hivemind;

import com.hivemind.controllers.MainController;
import javafx.application.Application;
import javafx.scene.paint.Color;

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

/*
* Object class
* Instantiated for each connection (ie: Each object represents two endpoints)
*
* TODO Functions
*  linkDest()   - This sets the child for the file directory.
*  linkSource() - This sets the parent for the file directory.
*  createConn() - Validates connection, also sets object to conn.
*  load() - this might be a protected name so may need renamed. this would init the while loop to check for socket.accept()
*
*/
public class SocketConn extends Main {
    // Port for the transfer
    private int port;
    // Directory that will be synced
    private String directoryPath;
    // Server socket
    private ServerSocket serverSocket;
    // Client IP address for authentication
    private InetAddress clientIP;
    // Socket for the incoming connection
    private Socket clientSocket;
    // WatchService that triggers on changes to the directory
    private WatchService watchService;
    // Authenticates subdirectories then adds them to the WatchService if valid
    private Map<WatchKey, Path> keyPathMap = new HashMap<>();
    // Amount of time between directory checks
    int timer;
    // Number of directories being watched. This is just for debugging.
    int count = 0;

    //Console themeing for easy diag
    public static final String GRAY = "\033[1;90m";
    public static final String RED = "\033[1;91m";
    public static final String GREEN = "\033[1;92m";
    public static final String SUCCESS = GREEN + "SUCCESS: " + GRAY;
    public static final String FAILURE = RED + "FAILURE: " + GRAY;

    public SocketConn(int port, int timer, String dir, InetAddress ip) {
        this.clientIP = ip;
        this.directoryPath = dir;
        this.port = port;
        this.timer = timer;

        //LogWindow.appendToLog("Message from AnotherClass");

        new Thread(() -> {
            try {
                System.out.println(GREEN + "-------------------------------------");
                System.out.println(GRAY + "Attempting to start socket:" + "\n" +
                                          "IP: " + clientIP + "\n" +
                                          "Port: " + port + "\n" +
                                          "Directory: " + directoryPath);
                System.out.println(GREEN + "-------------------------------------");
                serverSocket = new ServerSocket(port);
                registerWatchService(directoryPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void registerAll(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				register(dir);
                count++;
				return FileVisitResult.CONTINUE;
			}
		});
        System.out.println(SUCCESS + count + " directories have been registered with the watch service!");
    }

    private void register(Path dir) throws IOException {
        // Register the directory to fire events for modification, creation, and deletion of items.
        WatchKey key = dir.register(watchService, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);
        // Save this to the keypath to track.
        keyPathMap.put(key, dir);
        // After execution, print to console the outcome.
        //System.out.println(SUCCESS + "A new directory ( " + dir + " ) was registered!");
    }

    private void registerWatchService(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);
        this.watchService = FileSystems.getDefault().newWatchService();
        registerAll(path);

        new Thread(() -> {
            try {
                while (true) {
                    WatchKey key = watchService.take();

                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == ENTRY_MODIFY ||
                                event.kind() == ENTRY_CREATE) {

                            Path modifiedFile = (Path) event.context();
                            String fullFilePath = path.resolve(modifiedFile).toString();

                            if (Files.isDirectory(Path.of(fullFilePath))) {
                                registerAll(Path.of(fullFilePath));

                                System.out.println(": " + fullFilePath);

                                clientSocket = serverSocket.accept();
                                if (clientIP.equals(clientSocket.getInetAddress())) {
                                    sendDirectoryCreateRequest(modifiedFile.toString());
                                    System.out.println(SUCCESS + "Directory \"" + modifiedFile.getFileName() + "\" was sent to " + clientIP + "!");
                                } else {
                                    System.out.println(FAILURE + "Non-paired computer attempted connection!");
                                }

                            } else {
                                System.out.println("File modified: " + fullFilePath);
                                System.out.println("File name: " + modifiedFile.getFileName());

                                if (clientIP.equals(clientSocket.getInetAddress())) {
                                    sendFile(fullFilePath);
                                    System.out.println(SUCCESS + "File \"" + modifiedFile.getFileName() + "\" was sent to " + clientIP + "!");
                                } else {
                                    System.out.println(FAILURE + "Non-paired computer attempted connection!");
                                }
                            }

                        }
                        // If a file was deleted...
                        if (event.kind() == ENTRY_DELETE) {
                            Path modifiedFile = (Path) event.context();
                            String fullFilePath = path.resolve(modifiedFile).toString();

                            System.out.println("File deleted: " + fullFilePath);

                            clientSocket = serverSocket.accept();
                            sendDeleteRequest(fullFilePath);

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
                e.printStackTrace();
            }
        }).start();
    }

    private void sendDeleteRequest(String fullFilePath) throws IOException {
        File file = new File(fullFilePath);
        String filename = file.getName();

        OutputStream outputStream = clientSocket.getOutputStream();

        // Send the deletion flag
        outputStream.write(1);

        // Send the file name
        byte[] fileNameBytes = filename.getBytes(StandardCharsets.UTF_8);
        outputStream.write(fileNameBytes.length);
        outputStream.write(fileNameBytes);
    }

    private void sendFile(String fullFilePath) throws IOException {
        File file = new File(fullFilePath);
        String filename = file.getName();

        try (InputStream fileInputStream = new FileInputStream(file);
             OutputStream outputStream = clientSocket.getOutputStream()) {

            // Send the deletion flag (no deletion)
            outputStream.write(0);

            // Send the file name first
            byte[] fileNameBytes = filename.getBytes(StandardCharsets.UTF_8);
            outputStream.write(fileNameBytes.length);
            outputStream.write(fileNameBytes);

            // Send file content
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            System.out.println("File '" + filename + "' sent to client.");
        } finally {
            clientSocket.close(); // Ensure socket closure after sending file
        }
    }

    private void sendDirectoryCreateRequest(String directoryPath) {
        try (OutputStream outputStream = clientSocket.getOutputStream()) {

            // Send the directory creation flag
            outputStream.write(2);

            // Send the directory path
            byte[] directoryPathBytes = directoryPath.getBytes(StandardCharsets.UTF_8);
            outputStream.write(directoryPathBytes.length);
            outputStream.write(directoryPathBytes);

            System.out.println("Directory creation request sent for: " + directoryPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
