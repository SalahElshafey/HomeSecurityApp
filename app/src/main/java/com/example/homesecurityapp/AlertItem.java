package com.example.homesecurityapp;

public class AlertItem {
    private String message;
    private long timestamp;

    public AlertItem(String message, long timestamp) {
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

