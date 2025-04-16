package com.example.placementhub.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.placementhub.data.models.PlacementDrive;
import java.util.List;
import java.util.Date;

@Dao
public interface PlacementDriveDao {
    @Insert
    void insert(PlacementDrive drive);

    @Update
    void update(PlacementDrive drive);

    @Delete
    void delete(PlacementDrive drive);

    @Query("SELECT * FROM placement_drives")
    LiveData<List<PlacementDrive>> getAllDrives();

    @Query("SELECT * FROM placement_drives WHERE driveId = :driveId")
    LiveData<PlacementDrive> getDriveById(String driveId);

    @Query("SELECT * FROM placement_drives WHERE deadline >= :currentDate ORDER BY deadline ASC")
    LiveData<List<PlacementDrive>> getUpcomingDrives(Date currentDate);

    @Query("SELECT * FROM placement_drives WHERE deadline < :currentDate ORDER BY deadline DESC")
    LiveData<List<PlacementDrive>> getPastDrives(Date currentDate);

    @Query("SELECT * FROM placement_drives WHERE companyName LIKE '%' || :searchQuery || '%' OR jobRole LIKE '%' || :searchQuery || '%'")
    LiveData<List<PlacementDrive>> searchDrives(String searchQuery);
} 