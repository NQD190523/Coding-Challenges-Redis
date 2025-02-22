package main.com.codingchallenges.redisserver;

public class Serialise {
    
    public static String SerialiseString(String message) {
        String[] messagePart =  message.split(" ");
            return "+" + messagePart[0] + "\r\n";
    }
    public static String SerialiseError(String message) {
        String[] messagePart =  message.split(" ");
        return "-"+messagePart[0].toUpperCase()+ " " + messagePart[1] + "\r\n";
    }

    public static String SerialiseInteger(int message) {
        return ":" + message + "\r\n";
    }

    public static String SerialiseBulkString(String message) {
        return "$" +   (message.length() > 0 ? message.length() : 0 )  + "\r\n" + ( !message.isEmpty()? message : "") + "\r\n";
    }

    public static String SerialiseArray(String[] message) {
        StringBuilder newMessage = new StringBuilder() ;
        for (int i = 1; i < message.length; i++) {
            newMessage.append(message[i] + "\r\n");
        }
        return "*" +message.length+ "\r\n" + newMessage.toString();
    }


}