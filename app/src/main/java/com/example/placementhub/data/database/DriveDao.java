package com.example.placementhub.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.placementhub.data.models.Drive;
import java.util.List;

@Dao
public interface DriveDao {
    @Query("SELECT * FROM drives")
    List<Drive> getAllDrives();
    
    @Query("SELECT * FROM drives WHERE status = :status")
    List<Drive> getDrivesByStatus(String status);
    
    @Insert
    void insertDrive(Drive drive);
    
    @Update
    void updateDrive(Drive drive);
} 