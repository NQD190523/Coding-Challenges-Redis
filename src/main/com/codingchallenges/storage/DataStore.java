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
        public static Integer increase(Object key) {
            return (Integer) dataStore.compute(key, (k, v) -> {
                if (v == null || v.isExpired()) {
                    System.out.println("Not founded key: " + key +", create a new data with new key" );
                    return new ValueData(1L, -1);
                }
                if(!(v.value instanceof Integer)) throw new IllegalArgumentException("Value is not a Integer");
                return new ValueData(( (Integer) v.value) +1,v.expiryTime );
            }).value;
        }
        //DECR command
        public static Integer decrease(Object key) {
            return (Integer) dataStore.compute(key, (k, v) -> {
                if (v == null || v.isExpired()) {
                    System.out.println("Not founded key: " + key +", create a new data with new key" );
                    return new ValueData(-1L, -1);
                }
                if(!(v.value instanceof Integer)) throw new IllegalArgumentException("Value is not a Integer");
                return new ValueData(( (Integer) v.value) -1,v.expiryTime );
            }).value;
        }

        public static int lPush(Object key, Object... values) {
            @SuppressWarnings("unchecked")
            LinkedList<Object> list = (LinkedList<Object>) dataStore.compute(key, (k,v) ->{
                if (v == null || v.isExpired()) return new ValueData(new LinkedList<Object>(), -1);
            if (!(v.value instanceof LinkedList)) throw new IllegalArgumentException("ERR wrong type");
            return v;
            }).value;

            for (Object value : values) {
                list.addFirst(value);
            }
            return list.size();
        }

        public static int rPush(Object key, Object... values) {
            @SuppressWarnings("unchecked")
            LinkedList<Object> list = (LinkedList<Object>) dataStore.compute(key, (k,v) ->{
                if (v == null || v.isExpired()) return new ValueData(new LinkedList<Object>(), -1);
            if (!(v.value instanceof LinkedList)) throw new IllegalArgumentException("ERR wrong type");
            return v;
            }).value;

            for (Object value : values) {
                list.addLast(value);
            }
            return list.size();
        }

        public static void save(String filepath) {
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))){
                for (Object key : dataStore.keySet()) {
                    ValueData value = dataStore.get(key);
                    if(!value.isExpired()){
                        if(value.value instanceof LinkedList){
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
