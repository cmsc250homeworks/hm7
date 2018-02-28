package chatclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class ChatGateway implements chat.ChatConstants {

    private PrintWriter outputToServer;
    private BufferedReader inputFromServer;
    private TextArea textArea;

    // Establish the connection to the server.
    public ChatGateway(TextArea textArea) {
        this.textArea = textArea;
        try {
            // Create a socket to connect to the server
            Socket socket = new Socket("localhost", 8000);

            // Create an output stream to send data to the server
            outputToServer = new PrintWriter(socket.getOutputStream());

            // Create an input stream to read data from the server
            inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException ex) {
            Platform.runLater(() -> textArea.appendText("Exception in gateway constructor: " + ex.toString() + "\n"));
        }
    }
    
    public synchronized List<String> getRooms(){
        List<String> roomlist = new ArrayList();
        outputToServer.println(GET_ROOMLIST);
        outputToServer.flush();
        try{
            int size = Integer.parseInt(inputFromServer.readLine());
            for(int n = 0; n < size; n++){
                String name = inputFromServer.readLine();
                roomlist.add(name);
            }
        }catch (IOException ex) {
            Platform.runLater(() -> textArea.appendText("Error in getCommentCount: " + ex.toString() + "\n"));
        }
        return roomlist;
    }
    
    public synchronized void sendRoom(String r){
        outputToServer.println(SEND_ROOM);
        outputToServer.println(r);
        outputToServer.flush();
    }

    // Start the chat by sending in the user's handle.
    public synchronized void sendHandle(String handle) {
        outputToServer.println(SEND_HANDLE);
        outputToServer.println(handle);
        outputToServer.flush();
    }

    // Send a new comment to the server.
    public synchronized void sendComment(String comment) {
        outputToServer.println(SEND_COMMENT);
        outputToServer.println(comment);
        outputToServer.flush();
    }

    // Ask the server to send us a count of how many comments are
    // currently in the transcript.
    public synchronized int getCommentCount() {
        outputToServer.println(GET_COMMENT_COUNT);
        outputToServer.flush();
        int count = 0;
        try {
            count = Integer.parseInt(inputFromServer.readLine());
        } catch (IOException ex) {
            Platform.runLater(() -> textArea.appendText("Error in getCommentCount: " + ex.toString() + "\n"));
        }
        return count;
    }

    // Fetch comment n of the transcript from the server.
    public synchronized String getComment(int n) {
        outputToServer.println(GET_COMMENT);
        outputToServer.println(n);
        outputToServer.flush();
        String comment = "";
        try {
            comment = inputFromServer.readLine();
        } catch (IOException ex) {
            Platform.runLater(() -> textArea.appendText("Error in getComment: " + ex.toString() + "\n"));
        }
        return comment;
    }
}
