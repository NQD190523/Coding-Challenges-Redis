package main.com.codingchallenges.handlers;

import main.com.codingchallenges.protocol.Deserialiser;

public class handleOutput {
    
    public static String writeRespArray(String response) {
        String deserialisedResponse = "" ;
        switch(response.charAt(0)) {
            case '+':
                deserialisedResponse =  Deserialiser.DeserialiseString(response);
            case '-':
                return response.substring(1);
            case ':':
                return response.substring(1);
            case '$':
                return response.substring(1);
            case '*':
                return response.substring(1);
            default:
                return deserialisedResponse = "Invalid response";
        }
    }
}