package main.com.codingchallenges.handlers;

import java.io.BufferedReader;
import java.io.IOException;

public class handleInput {

    public static String[] readRespArray(BufferedReader reader) throws IOException {
        String firstLine = reader.readLine();
        if (firstLine == null) return null; // Client closed connection
        if (firstLine.startsWith("*")) {
            
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
        } else {
            String[] parts = firstLine.trim().split("\\s+", 2);
            if (parts.length == 1) {
                return new String[] { parts[0] };
            } else {
                return new String[] { parts[0], parts[1] };
            }
        }
    }
}