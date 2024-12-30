package com.example.homesecurityapp;

public class HistoryItem {
    private String lockStatus; // e.g., "Locked" or "Unlocked"
    private String safetyStatus; // e.g., "Safe" or "Not Safe"
    private long timestamp; // e.g., System.currentTimeMillis()

    // Default constructor (required for Firebase)
    public HistoryItem() {}

    // Constructor with parameters
    public HistoryItem(String lockStatus, String safetyStatus, long timestamp) {
        this.lockStatus = lockStatus;
        this.safetyStatus = safetyStatus;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(String lockStatus) {
        this.lockStatus = lockStatus;
    }

    public String getSafetyStatus() {
        return safetyStatus;
    }

    public void setSafetyStatus(String safetyStatus) {
        this.safetyStatus = safetyStatus;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
