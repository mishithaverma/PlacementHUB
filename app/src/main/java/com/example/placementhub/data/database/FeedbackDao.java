package com.example.placementhub.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.placementhub.data.models.Feedback;
import java.util.List;

@Dao
public interface FeedbackDao {
    @Query("SELECT * FROM feedback WHERE driveId = :driveId")
    List<Feedback> getFeedbackForDrive(int driveId);
    
    @Insert
    void insertFeedback(Feedback feedback);
} 