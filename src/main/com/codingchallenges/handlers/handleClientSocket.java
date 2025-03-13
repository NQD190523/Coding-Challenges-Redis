package main.com.codingchallenges.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class handleClientSocket {
    
    public static void handleClient(Socket clientSocket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            Socket socket = clientSocket // Auto-close socket
        ) {
            socket.setSoTimeout(5000);
            while (true) {
                String[] request = handleInput.readRespArray(reader);
                if (request == null) break; // Client disconnected
                System.out.println("Received request: " + String.join(", ", request));
                String response = handleRequest.handleRequests(request);
                System.out.println("Raw response: " + response.replace("\r\n", "\\r\\n"));
                // response = response.replace("\r\n", "\\r\\n");
                writer.write(response);
                writer.flush();
                System.out.println("Sent response: " + response);
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } 
    }
}