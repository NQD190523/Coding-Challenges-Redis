package main.com.codingchallenges.handlers;

import main.com.codingchallenges.protocol.Serialise;

public class handleRequest {

    public static String handleConnection(String[] request) {
        if (request == null || request.length == 0) {
            return Serialise.SerialiseError("ERR no command");
        }

        String command = request[0].toUpperCase();
        switch (command) {
            case "PING":
                if (request.length > 1) {
                    return Serialise.SerialiseBulkString(request[1]); // Echo argument
                }
                return Serialise.SerialiseString("PONG");

            case "SET":
                if (request.length != 3) {
                    return Serialise.SerialiseError("ERR wrong number of arguments for 'SET'");
                }
                // Store logic would go here (e.g., in a Map)
                return Serialise.SerialiseString("OK");

            case "GET":
                if (request.length != 2) {
                    return Serialise.SerialiseError("ERR wrong number of arguments for 'GET'");
                }
                // Retrieve logic would go here
                return Serialise.SerialiseBulkString("value"); // Placeholder

            default:
                return Serialise.SerialiseError("ERR unknown command '" + command + "'");
        }
    }
}
