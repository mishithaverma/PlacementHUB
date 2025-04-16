package com.example.placementhub.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.placementhub.data.models.StudentDrive;

import java.util.List;

/**
 * DAO for student-drive relationship operations
 */
@Dao
public interface StudentDriveDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(StudentDrive studentDrive);
    
    @Update
    void update(StudentDrive studentDrive);
    
    @Delete
    void delete(StudentDrive studentDrive);
    
    @Query("SELECT * FROM student_drives WHERE studentSapId = :studentSapId AND driveId = :driveId LIMIT 1")
    StudentDrive getStudentDrive(String studentSapId, String driveId);
    
    @Query("SELECT * FROM student_drives WHERE studentSapId = :studentSapId")
    LiveData<List<StudentDrive>> getStudentDrives(String studentSapId);
    
    @Query("SELECT * FROM student_drives WHERE driveId = :driveId")
    LiveData<List<StudentDrive>> getDriveStudents(String driveId);
    
    @Query("SELECT COUNT(*) FROM student_drives WHERE driveId = :driveId")
    int getStudentCountForDrive(String driveId);
    
    @Query("SELECT COUNT(*) FROM student_drives WHERE studentSapId = :studentSapId")
    LiveData<Integer> getDriveCountForStudent(String studentSapId);
    
    @Query("UPDATE student_drives SET applied = 1, status = 'applied' WHERE studentSapId = :studentSapId AND driveId = :driveId")
    void markDriveAsApplied(String studentSapId, String driveId);
    
    @Query("DELETE FROM student_drives WHERE studentSapId = :studentSapId AND driveId = :driveId")
    void removeStudentFromDrive(String studentSapId, String driveId);
} 