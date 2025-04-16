package com.example.placementhub.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "notifications")
public class Notification {
    @PrimaryKey
    @NonNull
    private String notificationId;

    @NonNull
    private String title;

    @NonNull
    private String message;

    @NonNull
    private String type;

    @NonNull
    private String targetAudience;

    private String targetId;
    private Date createdAt;
    private boolean isRead;
    private String priority;
    private String category;

    // Constructor
    public Notification(@NonNull String notificationId, @NonNull String title,
                       @NonNull String message, @NonNull String type,
                       @NonNull String targetAudience, String targetId,
                       Date createdAt, boolean isRead, String priority,
                       String category) {
        this.notificationId = notificationId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.targetAudience = targetAudience;
        this.targetId = targetId;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.priority = priority;
        this.category = category;
    }

    // Getters and Setters
    @NonNull
    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(@NonNull String notificationId) {
        this.notificationId = notificationId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getMessage() {
        return message;
    }

    public void setMessage(@NonNull String message) {
        this.message = message;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    @NonNull
    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(@NonNull String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
} 