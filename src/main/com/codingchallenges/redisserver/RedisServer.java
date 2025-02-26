package main.com.codingchallenges.redisserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import main.com.codingchallenges.handlers.handleRequest;


public class RedisServer {
    public static void main(String[] args) throws Exception {

        int port = 6379;
        InetSocketAddress address = new InetSocketAddress("localhost", port);
        ServerSocket serverSocket = new ServerSocket();

        try {
            serverSocket.bind(address);
            System.out.println("Server started on http://127.0.0.1:" + port);
        } catch (IOException e) {
            System.err.println("Error: Could not bind to port. " + e.getMessage());
            System.exit(1); // Stop execution if binding fails
        }
        
        // Ensure proper cleanup on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                serverSocket.close();
                System.out.println("Server shut down gracefully.");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }));


        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");
                threadPool.execute(() -> handleClient(clientSocket));
            } catch (IOException e) {
                System.err.println("Error accepting client: " + e.getMessage());
            }
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            Socket socket = clientSocket // Auto-close socket
        ) {
            while (true) {
                String[] request = readRespArray(reader);
                if (request == null) break; // Client disconnected

                System.out.println("Received request: " + String.join(", ", request));
                String response = handleRequest.handleConnection(request);
                writer.write(response);
                writer.flush();
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
    
    private static String[] readRespArray(BufferedReader reader) throws IOException {
        String firstLine = reader.readLine();
        if (firstLine == null) return null; // Client closed connection
        if (!firstLine.startsWith("*")) {
            throw new IOException("Expected RESP array, got: " + firstLine);
        }

        int elementCount = Integer.parseInt(firstLine.substring(1));
        if (elementCount == -1) return null; // Null array
        if (elementCount == 0) return new String[0]; // Empty array

        String[] elements = new String[elementCount];
        for (int i = 0; i < elementCount; i++) {
            String lengthLine = reader.readLine();
            if (lengthLine == null || !lengthLine.startsWith("$")) {
                throw new IOException("Expected bulk string, got: " + lengthLine);
            }
            int length = Integer.parseInt(lengthLine.substring(1));
            if (length == -1) {
                elements[i] = null;
            } else {
                String value = reader.readLine();
                if (value == null || value.length() != length) {
                    throw new IOException("Bulk string length mismatch or incomplete");
                }
                elements[i] = value;
            }
            // Consume the trailing \r\n after bulk string
            if (length != -1 && reader.readLine() == null) {
                throw new IOException("Missing trailing CRLF");
            }
        }
        return elements;
    }
}
