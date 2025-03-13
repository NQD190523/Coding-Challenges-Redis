package main.com.codingchallenges.handlers;

import main.com.codingchallenges.protocol.Serialise;
import main.com.codingchallenges.storage.DataStore;

public class handleRequest {

    public static String handleRequests(String[] request) {
        if (request == null || request.length == 0) {
            return Serialise.SerialiseError("ERR no command");
        }

        String command = request[0].toUpperCase();
         
        String[] args = request.length > 1 ? new String[request.length - 1] : new String[0];
        for (int i = 1; i < request.length; i++) {
            args[i - 1] = request[i];
            System.out.println("Arg " + (i - 1) + ": " + args[i - 1]); // Improved logging
        }
        System.out.println(Thread.currentThread().getName() + " Args length: " + args.length); // Debug
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
                return  desString;// e.g., "$11\r\nHello World\r\n"

            case "SET":
                if (args.length != 2) {
                    return Serialise.SerialiseError("ERR wrong number of arguments for 'SET' command");
                }
                DataStore.set(args[0], args[1]);
                return Serialise.SerialiseString("OK"); // "+OK\r\n"

            case "GET":
                if (args.length != 1) {
                    return Serialise.SerialiseError("ERR wrong number of arguments for 'GET' command");
                }
                String value = DataStore.get(args[0]);
                return Serialise.SerialiseBulkString(value); // "$n\r\nvalue\r\n" or "$-1\r\n"

            default:
                return Serialise.SerialiseError("ERR unknown command '" + command + "'");
        }
    }
}
