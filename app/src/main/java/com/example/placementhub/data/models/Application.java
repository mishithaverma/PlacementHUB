package com.example.placementhub.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "applications",
        foreignKeys = {
            @ForeignKey(entity = Student.class,
                    parentColumns = "sapId",
                    childColumns = "studentId",
                    onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = PlacementDrive.class,
                    parentColumns = "driveId",
                    childColumns = "driveId",
                    onDelete = ForeignKey.CASCADE)
        },
        indices = {
            @Index("studentId"),
            @Index("driveId")
        })
public class Application {
    @PrimaryKey
    @NonNull
    private String applicationId;

    @NonNull
    private String studentId;

    @NonNull
    private String driveId;

    @NonNull
    private Date applicationDate;

    @NonNull
    private String status;

    private String resumeUrl;
    private String coverLetter;
    private Date lastUpdated;
    private String remarks;

    // Constructor
    public Application(@NonNull String applicationId, @NonNull String studentId,
                      @NonNull String driveId, @NonNull Date applicationDate,
                      @NonNull String status, String resumeUrl, String coverLetter,
                      Date lastUpdated, String remarks) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.driveId = driveId;
        this.applicationDate = applicationDate;
        this.status = status;
        this.resumeUrl = resumeUrl;
        this.coverLetter = coverLetter;
        this.lastUpdated = lastUpdated;
        this.remarks = remarks;
    }

    // Getters and Setters
    @NonNull
    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(@NonNull String applicationId) {
        this.applicationId = applicationId;
    }

    @NonNull
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(@NonNull String studentId) {
        this.studentId = studentId;
    }

    @NonNull
    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(@NonNull String driveId) {
        this.driveId = driveId;
    }

    @NonNull
    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(@NonNull Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
} 