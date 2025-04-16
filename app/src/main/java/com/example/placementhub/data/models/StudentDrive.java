package com.example.placementhub.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

/**
 * Junction model class to link students with drives
 */
@Entity(
    tableName = "student_drives",
    primaryKeys = {"studentSapId", "driveId"},
    foreignKeys = {
        @ForeignKey(
            entity = Student.class,
            parentColumns = "sapId",
            childColumns = "studentSapId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = DriveInfo.class,
            parentColumns = "id",
            childColumns = "driveId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index("driveId")
    }
)
public class StudentDrive {
    @NonNull
    private String studentSapId;
    
    @NonNull
    private String driveId;
    
    // Status can be: notified, applied, rejected, selected, etc.
    private String status;
    
    // Whether the student has applied to this drive
    private boolean applied;
    
    // Default constructor
    public StudentDrive() {
    }
    
    // Getters and Setters
    @NonNull
    public String getStudentSapId() {
        return studentSapId;
    }
    
    public void setStudentSapId(@NonNull String studentSapId) {
        this.studentSapId = studentSapId;
    }
    
    @NonNull
    public String getDriveId() {
        return driveId;
    }
    
    public void setDriveId(@NonNull String driveId) {
        this.driveId = driveId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public boolean isApplied() {
        return applied;
    }
    
    public void setApplied(boolean applied) {
        this.applied = applied;
    }
} 