package com.example.homesecurityapp;

public class BuzzerItem {
    private String message;
    private long timestamp;

    public BuzzerItem(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

