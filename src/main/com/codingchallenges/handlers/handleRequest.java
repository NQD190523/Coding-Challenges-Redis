package main.com.codingchallenges.handlers;

import main.com.codingchallenges.protocol.Deserialiser;
import main.com.codingchallenges.protocol.Serialise;
import main.com.codingchallenges.storage.DataStore;

public class handleRequest {

    public static String handleRequests(String[] request) {
        if (request == null || request.length == 0) {
            return Serialise.SerialiseError("ERR no command");
        }

        String command = request[0].toUpperCase();
         
        String[] args = request[1].trim().split("\\s+");
        for (String arg : args) {
            System.out.println("Arg : " + arg);
        }
        switch (command) {

            case "PING":
                if (request.length > 1) {
                    return Serialise.SerialiseBulkString(request[1]); // e.g., "$5\r\nhello\r\n"
                } else {
                    return Serialise.SerialiseString("PONG"); // "+PONG\r\n"
                }

            case "ECHO":
                if (request.length != 2) {
                    return Serialise.SerialiseError("ERR wrong number of arguments for 'ECHO'");
                }
                
                String serialiseString = Serialise.SerialiseArray(args); 
                System.out.println("Echo command result : " + serialiseString);
                String desString = handleOutput.DeserialiseArrayToString(serialiseString);
                return  desString + "\r\n";// e.g., "$11\r\nHello World\r\n"

            case "SET":
                if (request.length != 3) {
                    return Serialise.SerialiseError("ERR wrong number of arguments for 'SET'");
                }
                // Store logic here
                DataStore.set(args[1], args[2]);
                return Serialise.SerialiseString("OK"); // "+OK\r\n"

            case "GET":
                if (request.length != 2) {
                    return Serialise.SerialiseError("ERR wrong number of arguments for 'GET'");
                }
                // Retrieve logic here
                String value = DataStore.get(args[1]);
                if (value == null) {
                    return Serialise.SerialiseBulkString(null); // "$-1\r\n"
                }
                return Deserialiser.DeserialiseBulkString(value); // "$5\r\nvalue\r\n"

            default:
                return Serialise.SerialiseError("ERR unknown command '" + command + "'");
        }
    }
}
