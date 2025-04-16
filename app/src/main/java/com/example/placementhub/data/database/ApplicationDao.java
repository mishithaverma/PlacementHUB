package com.example.placementhub.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.placementhub.data.models.Application;
import java.util.List;

@Dao
public interface ApplicationDao {
    @Insert
    void insert(Application application);

    @Update
    void update(Application application);

    @Delete
    void delete(Application application);

    @Query("SELECT * FROM applications")
    LiveData<List<Application>> getAllApplications();

    @Query("SELECT * FROM applications WHERE applicationId = :applicationId")
    LiveData<Application> getApplicationById(String applicationId);

    @Query("SELECT * FROM applications WHERE studentId = :studentId")
    LiveData<List<Application>> getApplicationsByStudent(String studentId);

    @Query("SELECT * FROM applications WHERE driveId = :driveId")
    LiveData<List<Application>> getApplicationsByDrive(String driveId);

    @Query("SELECT * FROM applications WHERE status = :status")
    LiveData<List<Application>> getApplicationsByStatus(String status);

    @Query("SELECT * FROM applications WHERE studentId = :studentId AND driveId = :driveId")
    LiveData<Application> getApplicationByStudentAndDrive(String studentId, String driveId);
} 