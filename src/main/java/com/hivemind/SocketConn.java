package com.hivemind;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

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
public class SocketConn {

    //What folder on the parent will we be syncing?
    String parentDir;
    //What folder on the child will these files be stored?
    String childDir;
    //Child ip address
    private InetAddress childIP;
    //Parent ip address
    private InetAddress parentIP;
    //Port used for connection
    private int socketPort;
    //Conn object
    private ServerSocket serverSocket;
    //Object status (ie is this instance the parent or the child?)'
    //True indicates parent, false indicates parent.
    private boolean status;
    //Amount of time between directory checks
    int timer;

    //Console themeing for easy diag
    public static final String SUCCESS = "\033[1;92m" + "SUCCESS: " + "\033[1;90m";
    public static final String FAILURE = "\033[1;91m" + "FAILURE: " + "\033[1;90m";


    public SocketConn(String parentDir, String childDir, InetAddress childIP, InetAddress parentIP, int socketPort, boolean status, int timer) {
        this.parentDir = parentDir;
        this.childDir = childDir;
        this.childIP = childIP;
        this.parentIP = parentIP;
        this.socketPort = socketPort;
        this.status = status;
        this.timer = timer;
        this.load();
    }

    //Todo
    // This function attempts a connection and passes a boolean for if the connection is valid
    // If it is valid, it assigns it to a variable for use in other functions.
    // This needs to be ran at every attempted connection since we need to verify that the IP is
    // valid.
    public boolean createConn() {
        try {
            // checks for parent or child conn...
            if (status) {
                // connect to parent
                serverSocket = new ServerSocket(socketPort, Integer.MAX_VALUE, parentIP);
            } else {
                // else connect to child
                serverSocket = new ServerSocket(socketPort, 50, childIP);
            }
            if (serverSocket.isBound()) {
                System.out.println(SUCCESS + "Connected to the server.");
                return true;
            }
        } catch (IOException e) {
            System.out.println(FAILURE + e);
        }
        return false;
    }

    public void load() {
        System.out.println(SUCCESS + "Loading!");
        if (this.createConn()) {
            Thread incoming = new Thread(() -> {
                while (true) {
                    System.out.println(SUCCESS);
                    try {
                        Socket test = this.serverSocket.accept();
                        System.out.println(SUCCESS + test.getInetAddress());

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            incoming.start();
        }
    }

    //adds the ability to change post creation
    public void linkDest(InetAddress ip, String dir) {
        childIP = ip;
        childDir = dir;
    }

    //adds the ability to change post creation
    public void linkSource(InetAddress ip, String dir) {
        parentIP = ip;
        parentDir = dir;
    }

    public void sendFile() throws IOException {

        if (createConn()) {
            Socket serverSocket = new Socket(this.parentIP, this.socketPort);
            InputStream in = serverSocket.getInputStream();
            OutputStream out = serverSocket.getOutputStream();

            File fileToSend = new File("C:\\Users\\gage1\\Documents\\GitHub\\JAVA3033-Hivemind\\src\\main\\resources\\com\\hivemind\\main.fxml");
            FileInputStream fileInputStream = new FileInputStream(fileToSend);
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            fileInputStream.close();
            serverSocket.close();

            /*Socket socket = serverSocket.accept();
            FileInputStream fis = new FileInputStream(file);

            BufferedInputStream bis = new BufferedInputStream(fis);

            //Get socket's output stream
            OutputStream os = socket.getOutputStream();

            //Read File Contents into contents array
            byte[] contents;
            long fileLength = file.length();
            long current = 0;

            while(current != fileLength) {
                int size = 10000;
                if(fileLength - current >= size)
                    current += size;
                else{
                    size = (int)(fileLength - current);
                    current = fileLength;
                }
                contents = new byte[size];
                bis.read(contents, 0, size);
                os.write(contents);
                System.out.print("Sending file ... " + (current*100) / fileLength + "% complete!");
            }
            os.flush();

            //File transfer done. Close the socket connection!
            socket.close();
            serverSocket.close();*/
            System.out.println(SUCCESS + "File sent!");
        }
    }

    public void recieveFile(String filename) throws IOException {
        if (createConn()) {
            Socket socket = serverSocket.accept();
            byte[] contents = new byte[10000];

            //Initialize the FileOutputStream to the output file's full path.
            FileOutputStream fos = new FileOutputStream(childDir + filename);

            BufferedOutputStream bos = new BufferedOutputStream(fos);

            InputStream is = socket.getInputStream();

            //No of bytes read in one read() call
            int bytesRead = 0;
            while ((bytesRead = is.read(contents)) != -1) {
                bos.write(contents, 0, bytesRead);
            }

            bos.flush();
            socket.close();
            System.out.println("File saved successfully!");
        }
    }
}
