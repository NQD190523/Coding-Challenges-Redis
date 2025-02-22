package test.com.codingchallenges.redisserver;


import org.junit.jupiter.api.Test;
import main.com.codingchallenges.redisserver.Serialise;


public class RedisResponseSerializerTest {
    
    @Test
    public void testSerialiseString() {
        // Serialise.SerialiseString("Hello World");
        String response = "Hello World";
        String expected = "+" + response + "\r\n";
        assert(expected.equals(Serialise.SerialiseString(expected)));
    }

    @Test
    public void testSerialiseError() {
        // Serialise.SerialiseError("Hello World");
        String response = "Hello World";
        String expected = "-"+ response.toUpperCase() + " " + response + "\r\n";
        assert(expected.equals(Serialise.SerialiseError(expected)));
    }

    @Test
    public void testSerialiseInteger() {
        // Serialise.SerialiseInteger(10);
        int response = 10;
        String expected = ":" + response + "\r\n";
        assert(expected.equals(Serialise.SerialiseInteger(response)));
    }

    @Test
    public void testSerialiseBulkString() {
        // Serialise.SerialiseBulkString("Hello World");
        String response = "Hello World";
        String expected = "$" + response.length() + "\r\n" + response + "\r\n";
        assert(expected.equals(Serialise.SerialiseBulkString(response)));   
    }

    @Test
    public void testSerialiseBulkStringWithEmptyString() {
        // Serialise.SerialiseBulkString("");
        String response = "";
        String expected = "$" + response.length() + "\r\n" + response + "\r\n";
        assert(expected.equals(Serialise.SerialiseBulkString(response)));   
    }

    @Test
    public void testSerialiseArray() {
        // Serialise.SerialiseArray(new String[]{"Hello", "World"});
        String[] response = new String[]{"Hello", "World"};
        StringBuilder newMessage = new StringBuilder() ;
        for (int i = 1; i < response.length; i++) {
            newMessage.append(response[i] + "\r\n");
        }
        String expected = "*" + response.length + "\r\n" + newMessage.toString();
        assert(expected.equals(Serialise.SerialiseArray(response)));
    }

    @Test
    public void testSerialiseArrayWithEmptyString() {
        // Serialise.SerialiseArray(new String[]{"Hello", ""});
        String[] response = new String[]{"Hello", ""};
        StringBuilder newMessage = new StringBuilder() ;
        for (int i = 1; i < response.length; i++) {
            newMessage.append(response[i] + "\r\n");
        }
        String expected = "*" + response.length + "\r\n" + newMessage.toString();
        assert(expected.equals(Serialise.SerialiseArray(response)));
    }
}