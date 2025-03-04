package main.com.codingchallenges.protocol;


public class Serialise {
    
    public static String SerialiseString(String message) {
        return "+" + message + "\r\n";
    }
    public static String SerialiseError(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "-ERR\r\n"; // Default error if input is invalid
        }
        return "-" + message.trim() + "\r\n"; // Keep it simple, or uppercase if needed
    }

    public static String SerialiseInteger(int message) {
        return ":" + message + "\r\n";
    }

    public static String SerialiseBulkString(String message) {
        if (message == null) {
            return "$-1\r\n"; // Null in RESP
        }
        return "$" + message.length() + "\r\n" + message + "\r\n";
    }

    public static String SerialiseArray(Object[] message) {

        if (message == null) {
            return "*-1\r\n"; // Null array in RESP
        }
        StringBuilder newMessage = new StringBuilder();
        newMessage.append("*").append(message.length).append("\r\n");
        for (Object element : message) {
            if (element instanceof Integer) {
                newMessage.append(SerialiseInteger((Integer) element));
            } else if (element instanceof String) {
                newMessage.append(SerialiseBulkString((String) element));
            } else {
                newMessage.append(SerialiseBulkString(null)); // Treat unknown/null as RESP null
            }
        }
        return newMessage.toString();
    }


}