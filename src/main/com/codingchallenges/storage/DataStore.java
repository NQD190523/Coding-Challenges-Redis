package main.com.codingchallenges.storage;

import java.util.HashMap;

public class DataStore {

        public static final HashMap<String, String> dataStore = new HashMap<>();
        
        public static void set(String key, String value) {
            dataStore.put(key, value);
            System.out.println("SET: Stored " + key + " = " + value);
        }
    
        public static String get(String key) {
            String value = dataStore.get(key);
            System.out.println("GET: Retrieved " + key + " = " + (value != null ? value : "null"));
            return value;
        }
}
