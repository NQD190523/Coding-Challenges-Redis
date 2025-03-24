package main.com.codingchallenges.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class handleInput {

    public static Object[] readRespArray(BufferedReader reader) throws IOException {
        Object firstLine = reader.readLine().replace("\r\n", "\\r\\n");
        if (firstLine == null) return null; // Client closed connection
        System.out.println("Raw input: " + firstLine); // Debug
        if (firstLine.toString().startsWith("*")) {
            
            int elementCount = Integer.parseInt(firstLine.toString().substring(1));
            if (elementCount == -1) return null; // Null array
            if (elementCount == 0) return new Object[0]; // Empty array
    
            Object[] elements = new Object[elementCount];
            for (int i = 0; i < elementCount; i++) {
                Object lengthLine = reader.readLine();
                if (lengthLine == null) {
                    System.out.println(Thread.currentThread().getName() + " Incomplete array: null length at element " + i);
                    return null; // Partial disconnect
                }
                System.out.println(Thread.currentThread().getName() + " Element " + i + " length: " + lengthLine);
                if (!lengthLine.toString().startsWith("$")) {
                    throw new IOException("Expected bulk string, got: " + lengthLine);
                }
                int length = Integer.parseInt(lengthLine.toString().substring(1));
                if (length == -1) {
                    elements[i] = null;
                } else {
                    Object value = reader.readLine();
                    if (value == null) {
                        System.out.println(Thread.currentThread().getName() + " Incomplete array: null value at element " + i);
                        return null; // Partial disconnect
                    }
                    System.out.println(Thread.currentThread().getName() + " Element " + i + " value: " + value);
                    if (value.toString().length() != length) {
                        throw new IOException("Bulk string length mismatch or incomplete");
                    }
                    elements[i] = value;
                }
            }
            return elements;
        } else {
            List<Object> parts = new ArrayList<Object>();
            for (Object eObject : firstLine.toString().trim().split("\\s+", 2)) {
                if (eObject instanceof String) {
                    parts.add(eObject.toString());
                }
                if(eObject instanceof Integer){
                    parts.add(Integer.parseInt(eObject.toString()));
                }
            }
            if (parts.size() == 1) {
                return new Object[] { parts.get(0) };
            } else {
                return new Object[] { parts.get(0), parts.get(1) };
            }
        }
    }
}