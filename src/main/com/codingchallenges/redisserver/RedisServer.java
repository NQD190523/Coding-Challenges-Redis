package main.com.codingchallenges.redisserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import main.com.codingchallenges.handlers.handleClientSocket;


public class RedisServer {
    public static void main(String[] args) throws Exception {

        int port = 6380;
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
        // Create a thread pool for handling client connections
        ExecutorService threadPool = Executors.newFixedThreadPool(50);
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");
                threadPool.execute(() -> handleClientSocket.handleClient(clientSocket));
            } catch (IOException e) {
                System.err.println("Error accepting client: " + e.getMessage());
            }
        }
    }
}
