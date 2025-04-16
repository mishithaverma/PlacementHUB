package com.example.placementhub.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.placementhub.data.models.DriveInfo;

import java.util.List;

/**
 * DAO for drive-related database operations
 */
@Dao
public interface DriveDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(DriveInfo drive);
    
    @Update
    void update(DriveInfo drive);
    
    @Delete
    void delete(DriveInfo drive);
    
    @Query("SELECT * FROM drives WHERE id = :driveId LIMIT 1")
    DriveInfo getDriveById(String driveId);
    
    @Query("SELECT * FROM drives ORDER BY driveDate DESC")
    LiveData<List<DriveInfo>> getAllDrives();
    
    @Query("SELECT * FROM drives WHERE status = :status ORDER BY driveDate ASC")
    LiveData<List<DriveInfo>> getDrivesByStatus(String status);
    
    @Query("SELECT COUNT(*) FROM drives")
    int getDriveCount();
    
    // Get drives for a student by joining with student_drives table
    @Query("SELECT d.* FROM drives d " +
           "INNER JOIN student_drives sd ON d.id = sd.driveId " +
           "WHERE sd.studentSapId = :studentSapId")
    LiveData<List<DriveInfo>> getDrivesForStudent(String studentSapId);
    
    // Get upcoming drives for a student
    @Query("SELECT d.* FROM drives d " +
           "INNER JOIN student_drives sd ON d.id = sd.driveId " +
           "WHERE sd.studentSapId = :studentSapId AND d.status = 'upcoming'")
    LiveData<List<DriveInfo>> getUpcomingDrivesForStudent(String studentSapId);
    
    // Get ongoing drives for a student
    @Query("SELECT d.* FROM drives d " +
           "INNER JOIN student_drives sd ON d.id = sd.driveId " +
           "WHERE sd.studentSapId = :studentSapId AND d.status = 'ongoing'")
    LiveData<List<DriveInfo>> getOngoingDrivesForStudent(String studentSapId);
    
    // Get completed drives for a student
    @Query("SELECT d.* FROM drives d " +
           "INNER JOIN student_drives sd ON d.id = sd.driveId " +
           "WHERE sd.studentSapId = :studentSapId AND d.status = 'done'")
    LiveData<List<DriveInfo>> getCompletedDrivesForStudent(String studentSapId);
} 