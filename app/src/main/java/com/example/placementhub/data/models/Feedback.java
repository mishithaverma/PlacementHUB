package com.example.placementhub.data.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "feedback",
        foreignKeys = @ForeignKey(
            entity = Drive.class,
            parentColumns = "id",
            childColumns = "driveId",
            onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("driveId")})
public class Feedback {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private int driveId;
    private int rating;
    private boolean roleClear;
    private String issuesFaced;
    private String suggestions;
    private boolean futureParticipation;
    private Date submissionDate;
    
    // Default constructor
    public Feedback() {
        this.submissionDate = new Date();
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getDriveId() {
        return driveId;
    }
    
    public void setDriveId(int driveId) {
        this.driveId = driveId;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public boolean isRoleClear() {
        return roleClear;
    }
    
    public void setRoleClear(boolean roleClear) {
        this.roleClear = roleClear;
    }
    
    public String getIssuesFaced() {
        return issuesFaced;
    }
    
    public void setIssuesFaced(String issuesFaced) {
        this.issuesFaced = issuesFaced;
    }
    
    public String getSuggestions() {
        return suggestions;
    }
    
    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }
    
    public boolean isFutureParticipation() {
        return futureParticipation;
    }
    
    public void setFutureParticipation(boolean futureParticipation) {
        this.futureParticipation = futureParticipation;
    }
    
    public Date getSubmissionDate() {
        return submissionDate;
    }
    
    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }
} 