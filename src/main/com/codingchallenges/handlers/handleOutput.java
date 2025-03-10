package main.com.codingchallenges.handlers;

import main.com.codingchallenges.protocol.Deserialiser;

public class handleOutput {

    public static String DeserialiseArrayToString(String message) {
        Object[] array = Deserialiser.DeserialiseArray(message);
        if (array == null) return null; // Or "" if preferred
        return String.join(" ", java.util.Arrays.stream(array)
            .map(obj -> obj == null ? "null" : obj.toString())
            .toArray(String[]::new));
    }
    
}