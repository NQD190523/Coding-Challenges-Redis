package main.com.codingchallenges.handlers;

import main.com.codingchallenges.protocol.Serialise;
import main.com.codingchallenges.storage.DataStore;

public class handleRequest {

    public static String handleRequests(Object[] request) {
        if (request == null || request.length == 0) {
            return Serialise.SerialiseError("ERR no command");
        }

        String command = request[0].toString().toUpperCase();
        Object[] args = request.length > 1 ? new Object[request.length - 1] : new Object[0];
        for (int i = 1; i < request.length; i++) {
            args[i - 1] = request[i];
            System.out.println("Arg " + (i - 1) + ": " + args[i - 1]); // Improved logging
        }
        System.out.println(Thread.currentThread().getName() + " Args length: " + args.length); // Debug
        switch (command) {
            case "PING":
                if (request.length > 1) {
                    return Serialise.SerialiseBulkString(request[1].toString()); // e.g., "$5\r\nhello\r\n"
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
                if (args.length < 2) {
                    return Serialise.SerialiseError("ERR wrong number of arguments for 'SET' command");
                }
                Object key = args[0];
                Object values = args[1];
                long expiry = -1; // Default: no expiry
            
                // Check if there are optional expiry arguments
                if (args.length > 2) {
                    // Ensure pairs of option and value (e.g., EX 10)
                    if (args.length % 2 != 0) {
                        return Serialise.SerialiseError("ERR syntax error: missing value for option");
                    }
                    // Parse options starting from args[2]
                    for (int i = 2; i < args.length; i += 2) {
                        String option = args[i].toString().toUpperCase();
                        String arg = args[i + 1].toString();
                        try {
                            switch (option) {
                                case "EX":
                                    expiry = System.currentTimeMillis() + (Long.parseLong(arg) * 1000);
                                    break;
                                case "PX":
                                    expiry = System.currentTimeMillis() + Long.parseLong(arg);
                                    break;
                                case "EXAT":
                                    expiry = Long.parseLong(arg) * 1000; // Seconds to milliseconds
                                    break;
                                case "PXAT":
                                    expiry = Long.parseLong(arg); // Absolute milliseconds
                                    break;
                                default:
                                    return Serialise.SerialiseError("ERR unknown option '" + option + "'");
                            }
                        } catch (NumberFormatException e) {
                            return Serialise.SerialiseError("ERR invalid expiry value: " + arg);
                        }
                    }
                }
                DataStore.set(key, values, expiry);
                return Serialise.SerialiseString("OK");
            case "GET":
                if (args.length != 1) {
                    return Serialise.SerialiseError("ERR wrong number of arguments for 'GET' command");
                }
                
                String value = DataStore.get(args[0]).toString();
                return Serialise.SerialiseBulkString(value); // "$n\r\nvalue\r\n" or "$-1\r\n"
            case "DEL":
            if (args.length != 1) {
                    return Serialise.SerialiseError("ERR wrong number of arguments for 'DEL' command");
                }
                DataStore.delete(args[0]);
                return Serialise.SerialiseString("OK"); // "+OK\r\n"
            case "EXISTS":
                if (args.length != 1) {
                    return Serialise.SerialiseError("ERR wrong number of arguments for 'EXISTS' command");
                }
                boolean exists = DataStore.isExists(args[0]);
                return Serialise.SerialiseInteger(  (exists ? 1L : 0)); // ":1\r\n" or ":0\r\n"
            case "INCR":
            if (args.length != 1) {
                    return Serialise.SerialiseError("ERR wrong number of arguments for 'INCR' command");
                }
                Long incr = DataStore.increase(args[0]);
                return Serialise.SerialiseInteger(incr); // ":n\r\n"
            case "DECR":
            if (args.length != 1) {
                    return Serialise.SerialiseError("ERR wrong number of arguments for 'DECR' command");
                }
                Long decr = DataStore.decrease(args[0]);
                return Serialise.SerialiseInteger(decr); // ":n\r\n"
            case "LPUSH":
            if (args.length < 2) {
                    return Serialise.SerialiseError("ERR wrong number of arguments for 'LPUSH' command");
                }
                Long leftListLength =  DataStore.lPush(args[0], args[1]);
                return Serialise.SerialiseInteger((Long) leftListLength); // ":n\r\n"
            case "RPUSH":
            if (args.length < 2) {
                    return Serialise.SerialiseError("ERR wrong number of arguments for 'RPUSH' command");
                }
                Long rightListLength =  DataStore.rPush(args[0], args[1]);
                return Serialise.SerialiseInteger(rightListLength); // ":n\r\n"
            default:
                return Serialise.SerialiseError("ERR unknown command '" + command + "'");

        }
    }
}
