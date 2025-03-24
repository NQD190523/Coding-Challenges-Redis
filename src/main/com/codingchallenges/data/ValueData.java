package main.com.codingchallenges.data;

public class ValueData {
    public Object value;
    public long expiryTime;

    public ValueData(Object value, long expiryTime) {
        this.value = value;
        this.expiryTime = expiryTime;
    }

    public boolean isExpired() {
        return this.expiryTime != -1 && System.currentTimeMillis() > this.expiryTime;
    }
}