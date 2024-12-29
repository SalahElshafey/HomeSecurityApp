package com.example.homesecurityapp;

public class HistoryItem {
    private String lockStatus;
    private String safetyStatus;
    private long timestamp;

    // Default constructor (required for Firebase)
    public HistoryItem() {}

    // Constructor to initialize fields
    public HistoryItem(String lockStatus, String safetyStatus, long timestamp) {
        this.lockStatus = lockStatus;
        this.safetyStatus = safetyStatus;
        this.timestamp = timestamp;
    }

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
