package main.com.codingchallenges.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import main.com.codingchallenges.data.ValueData;

public class DataStore {

        public static final ConcurrentHashMap<Object, ValueData> dataStore = new ConcurrentHashMap<>();
        
        //SET command
        public static void set(Object key, Object value, long expiryTime) {
            dataStore.put(key, new ValueData(value, expiryTime));
            System.out.println("SET: Stored " + key + " = " + value);
        }
        //GET command
        public static Object get(Object key) {
            ValueData value = dataStore.get(key);
            if (value == null) {
                System.out.println("GET: Retrieved " + key + " = null");
                return null;
            }
            //Passive Expiry
            if (value.isExpired()) {
                dataStore.remove(key);
                return null;
            }
            System.out.println("GET: Retrieved " + key + " = " + (value != null ? value.value : "null"));
            return value.value;
        }
        //DEL command
        public static void delete(Object key) {
            if ( isExists(key) ) {
                dataStore.remove(key);
                System.out.println("DEL: Deleted " + key);
            }
        }
        //EXISTS command
        public static boolean isExists(Object key) {
            if (dataStore.containsKey(key)) {
                System.out.println("EXISTS: " + key + " exists");
                return true;
            } else {
                System.out.println("EXISTS: " + key + " does not exist");
                return false;
            }
        }
        //INCR command
        public static Long increase(Object key) {
            return (Long) dataStore.compute(key, (k, v) -> {
                if (v == null || v.isExpired()) {
                    System.out.println("Not founded key: " + key +", create a new data with new key" );
                    return new ValueData(1L, -1);
                }
                if (v.value instanceof Long) {
                    return new ValueData(((Long) v.value) + 1, v.expiryTime);
                }
                if (v.value instanceof Integer) {
                    return new ValueData(((Integer) v.value) + 1L, v.expiryTime);
                }
                if (v.value instanceof String) {
                    try {
                        long val = Long.parseLong((String) v.value);
                        return new ValueData(val + 1, v.expiryTime);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("ERR value is not an integer or out of range");
                    }
                }
                throw new IllegalArgumentException("ERR wrong type");
            }).value;
        }
        //DECR command
        public static Long decrease(Object key) {
            return (Long) dataStore.compute(key, (k, v) -> {
                if (v == null || v.isExpired()) {
                    System.out.println("Not founded key: " + key +", create a new data with new key" );
                    return new ValueData(1L, -1);
                }
                if (v.value instanceof Long) {
                    return new ValueData(((Long) v.value) - 1, v.expiryTime);
                }
                if (v.value instanceof Integer) {
                    return new ValueData(((Integer) v.value) - 1L, v.expiryTime);
                }
                if (v.value instanceof String) {
                    try {
                        long val = Long.parseLong((String) v.value);
                        return new ValueData(val - 1, v.expiryTime);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("ERR value is not an integer or out of range");
                    }
                }
                throw new IllegalArgumentException("ERR wrong type");
            }).value;
        }

        public static long lPush(Object key, Object... values) {
            ValueData updatedValue = dataStore.compute(key, (k, v) -> {
                // Create new list if null or expired, otherwise get existing list
                @SuppressWarnings("unchecked")
                LinkedList<Object> currentList = (v == null || v.isExpired()) 
                    ? new LinkedList<>() 
                    : (v.value instanceof LinkedList) 
                        ? (LinkedList<Object>) v.value 
                        : null;
        
                // Check type and throw error if not a list
                if (currentList == null) {
                    throw new IllegalArgumentException("ERR wrong type");
                }
        
                // Add values to the head of the list
                for (Object value : values) {
                    currentList.addFirst(String.valueOf(value));
                }
        
                // Return updated ValueData with existing expiry or default -1
                return new ValueData(currentList, v == null ? -1 : v.expiryTime);
            });
            return ((LinkedList<?>) updatedValue.value).size();
        }

        public static long rPush(Object key, Object... values) {
            ValueData updatedValue = dataStore.compute(key, (k, v) -> {
                // Create new list if null or expired, otherwise get existing list
                @SuppressWarnings("unchecked")
                LinkedList<Object> currentList = (v == null || v.isExpired()) 
                    ? new LinkedList<>() 
                    : (v.value instanceof LinkedList) 
                        ? (LinkedList<Object>) v.value 
                        : null;
        
                // Check type and throw error if not a list
                if (currentList == null) {
                    throw new IllegalArgumentException("ERR wrong type");
                }
        
                // Add values to the head of the list
                for (Object value : values) {
                    currentList.addLast(String.valueOf(value));
                }
        
                // Return updated ValueData with existing expiry or default -1
                return new ValueData(currentList, v == null ? -1 : v.expiryTime);
            });
            return ((LinkedList<?>) updatedValue.value).size();
        }

        public static void save(String filepath) {
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))){
                for (Object key : dataStore.keySet()) {
                    ValueData value = dataStore.get(key);
                    if(!value.isExpired()){
                        if(value.value instanceof LinkedList){
                            @SuppressWarnings("unchecked")
                            LinkedList<String> list = (LinkedList<String>) value.value;
                            writer.write("L|" +key + "|" + String.join(",", list) + "|" + value.expiryTime );
                        } else if(value.value instanceof String){
                            writer.write("S|" + key + "|" + value.value + "|" + value.expiryTime );
                        } else if(value.value instanceof Long){
                            writer.write("I|" + key + "|" + value.value + "|" + value.expiryTime );
                        }
                        writer.newLine(); 
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } 
        }


        public static void load(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 4);
                if (parts.length != 4) continue;
                String type = parts[0];
                String key = parts[1];
                String value = parts[2];
                long expiry = Long.parseLong(parts[3]);
                if (expiry != -1 && System.currentTimeMillis() > expiry) continue;
                switch (type) {
                    case "S": 
                        dataStore.put(key, new ValueData(value, expiry)); 
                        break;
                    case "I": 
                        dataStore.put(key, new ValueData(Long.parseLong(value), expiry)); break;
                    case "L":
                        LinkedList<String> list = new LinkedList<>();
                        if (!value.isEmpty()) {
                            String[] elements = value.split(",");
                            for (String element : elements) list.add(element);
                        }
                        dataStore.put(key, new ValueData(list, expiry));
                        break;
                }
            }
        }
    }

}
