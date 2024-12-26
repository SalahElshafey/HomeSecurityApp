package com.example.homesecurityapp;

public class HistoryItem {
    private String status;
    private String timestamp;

    public HistoryItem(String status, String timestamp) {
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
