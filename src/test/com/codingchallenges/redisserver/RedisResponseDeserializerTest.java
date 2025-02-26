package test.com.codingchallenges.redisserver;


import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import main.com.codingchallenges.redisserver.Deserialiser;

public class RedisResponseDeserializerTest {
    
   @Test
    public void testDeserialiseString() {
        assertEquals("OK", Deserialiser.DeserialiseString("+OK\r\n"));
    }

    @Test
    public void testDeserialiseError() {
        Exception exception = Deserialiser.DeserialiseError("-Error message\r\n");
        assertEquals("Error message", exception.getMessage());
    }

    @Test
    public void testDeserialiseInteger() {
        assertEquals(100, Deserialiser.DeserialiseInteger(":100\r\n"));
    }

    @Test
    public void testDeserialiseBulkString() {
        assertEquals("Hello", Deserialiser.DeserialiseBulkString("$5\r\nHello\r\n"));
    }

    @Test
    public void testDeserialiseNullBulkString() {
        assertNull(Deserialiser.DeserialiseBulkString("$-1\r\n"));
    }

    @Test
    public void testDeserialiseArray() {
        String input = "*2\r\n$5\r\nHello\r\n$5\r\nWorld\r\n";
        String[] expected = {"Hello", "World"};
        assertArrayEquals(expected, Deserialiser.DeserialiseArray(input));
    }

    @Test
    public void testDeserialiseEmptyArray() {
        assertNull(Deserialiser.DeserialiseArray("*0\r\n"));
    }

    
}