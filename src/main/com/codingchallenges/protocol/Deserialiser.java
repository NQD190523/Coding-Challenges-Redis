package main.com.codingchallenges.redisserver;


public class Deserialiser {
    
    public static String DeserialiseString(String message) {
        if (message == null || !message.startsWith("+") || !message.endsWith("\r\n")) {
            throw new IllegalArgumentException("Invalid RESP simple string: " + message);
        }
        return message.substring(1, message.length() - 2);
    }

    public static Exception DeserialiseError(String message) {
        if (message == null || !message.startsWith("-") || !message.endsWith("\r\n")) {
            throw new IllegalArgumentException("Invalid RESP error: " + message);
        }
        return new RuntimeException(message.substring(1, message.length() - 2));
    }

    public static int DeserialiseInteger(String message) {
        if (message == null || !message.startsWith(":") || !message.endsWith("\r\n")) {
            throw new IllegalArgumentException("Invalid RESP integer: " + message);
        }
        try {
            return Integer.parseInt(message.substring(1, message.length() - 2));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer value in RESP: " + message, e);
        }
    }

    public static String DeserialiseBulkString(String message) {
        if (message == null || !message.startsWith("$")) {
            throw new IllegalArgumentException("Invalid RESP bulk string: " + message);
        }
        int firstCrlf = message.indexOf("\r\n");
        if (firstCrlf == -1 || !message.endsWith("\r\n")) {
            throw new IllegalArgumentException("Malformed RESP bulk string: " + message);
        }
        int length = Integer.parseInt(message.substring(1, firstCrlf));
        if (length == -1) {
            return null; // Null bulk string
        }
        String value = message.substring(firstCrlf + 2, message.length() - 2);
        if (value.length() != length) {
            throw new IllegalArgumentException("Bulk string length mismatch: declared " + length + ", actual " + value.length());
        }
        return value;
    }

    public static Object[] DeserialiseArray(String message) {
        if (message == null || !message.startsWith("*")) {
            throw new IllegalArgumentException("Invalid RESP array: " + message);
        }
        String[] lines = message.split("\r\n", -1); // Include empty lines
        int length = Integer.parseInt(lines[0].substring(1));
        if (length == -1) return null; // Null array
        if (length == 0) return new Object[0]; // Empty array
    
        Object[] array = new Object[length];
        int lineIndex = 1;
        for (int i = 0; i < length && lineIndex < lines.length; i++) {
            String line = lines[lineIndex];
            if (line.startsWith("$")) {
                int bulkLength = Integer.parseInt(line.substring(1));
                lineIndex++;
                if (bulkLength == -1) {
                    array[i] = null;
                } else {
                    array[i] = lines[lineIndex];
                    if (lines[lineIndex].length() != bulkLength) {
                        throw new IllegalArgumentException("Bulk string length mismatch at index " + i);
                    }
                }
            } else if (line.startsWith(":")) {
                array[i] = Integer.parseInt(line.substring(1));
            } else if (line.startsWith("+")) {
                array[i] = line.substring(1);
            } else {
                throw new IllegalArgumentException("Unsupported RESP type in array at line " + lineIndex + ": " + line);
            }
            lineIndex++;
        }
        return array;
    }
}