package main.com.codingchallenges.handlers;

public class parseInput {
    
    public static Object[] parse(String input) {
        String[] parts = input.trim().split("\\s+"); // Split by whitespace
        Object[] result = new Object[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                // Try to parse as an integer if possible
                result[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                // Otherwise, treat it as a string
                result[i] = parts[i];
            }
        }
        return result;
    }
}