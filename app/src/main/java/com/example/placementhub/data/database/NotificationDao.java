package com.example.placementhub.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.placementhub.data.models.Notification;
import java.util.Date;
import java.util.List;

@Dao
public interface NotificationDao {
    @Insert
    void insert(Notification notification);

    @Update
    void update(Notification notification);

    @Delete
    void delete(Notification notification);

    @Query("SELECT * FROM notifications")
    LiveData<List<Notification>> getAllNotifications();

    @Query("SELECT * FROM notifications WHERE notificationId = :notificationId")
    LiveData<Notification> getNotificationById(String notificationId);

    @Query("SELECT * FROM notifications WHERE type = :type")
    LiveData<List<Notification>> getNotificationsByType(String type);

    @Query("SELECT * FROM notifications WHERE targetAudience = :targetAudience")
    LiveData<List<Notification>> getNotificationsByTargetAudience(String targetAudience);

    @Query("SELECT * FROM notifications WHERE createdAt >= :startDate AND createdAt <= :endDate")
    LiveData<List<Notification>> getNotificationsByDateRange(Date startDate, Date endDate);

    @Query("SELECT * FROM notifications ORDER BY createdAt DESC LIMIT :limit")
    LiveData<List<Notification>> getRecentNotifications(int limit);
} 