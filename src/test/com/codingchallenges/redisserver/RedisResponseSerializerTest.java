package test.com.codingchallenges.redisserver;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import main.com.codingchallenges.protocol.Serialise;


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
        Long response = 10L;
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
        // Test with mixed types: String and Integer
        Object[] response = new Object[]{"Hello", 123, "World"};

        // Expected RESP serialized output
        StringBuilder expectedMessage = new StringBuilder();
        expectedMessage.append("*").append(response.length).append("\r\n");

        for (Object element : response) {
            if (element instanceof Integer) {
                expectedMessage.append(Serialise.SerialiseInteger((Long) element));
            } else if (element instanceof String) {
                expectedMessage.append(Serialise.SerialiseBulkString((String) element));
            }
        }

        // Assert expected output matches actual output
        assertEquals(expectedMessage.toString(), Serialise.SerialiseArray(response));
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