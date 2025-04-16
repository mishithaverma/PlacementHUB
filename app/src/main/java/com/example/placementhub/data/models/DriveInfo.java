package com.example.placementhub.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Model class for company drive information
 */
@Entity(tableName = "drives")
public class DriveInfo {
    @PrimaryKey
    @NonNull
    private String id;
    
    private String companyName;
    private String jobTitle;
    private String location;
    private String packageOffered;
    private String jobDescription;
    
    private String industryType;
    private String workType;
    private String jobType;
    private String interviewMode;
    private String driveMode;
    
    private String startDate;
    private String deadline;
    private String driveDate;
    
    // Drive status: upcoming, ongoing, done
    private String status;
    
    // Default constructor required by Room
    public DriveInfo() {
    }
    
    // Getters and Setters
    @NonNull
    public String getId() {
        return id;
    }
    
    public void setId(@NonNull String id) {
        this.id = id;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getJobTitle() {
        return jobTitle;
    }
    
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getPackageOffered() {
        return packageOffered;
    }
    
    public void setPackageOffered(String packageOffered) {
        this.packageOffered = packageOffered;
    }
    
    public String getJobDescription() {
        return jobDescription;
    }
    
    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
    
    public String getIndustryType() {
        return industryType;
    }
    
    public void setIndustryType(String industryType) {
        this.industryType = industryType;
    }
    
    public String getWorkType() {
        return workType;
    }
    
    public void setWorkType(String workType) {
        this.workType = workType;
    }
    
    public String getJobType() {
        return jobType;
    }
    
    public void setJobType(String jobType) {
        this.jobType = jobType;
    }
    
    public String getInterviewMode() {
        return interviewMode;
    }
    
    public void setInterviewMode(String interviewMode) {
        this.interviewMode = interviewMode;
    }
    
    public String getDriveMode() {
        return driveMode;
    }
    
    public void setDriveMode(String driveMode) {
        this.driveMode = driveMode;
    }
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    
    public String getDeadline() {
        return deadline;
    }
    
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
    
    public String getDriveDate() {
        return driveDate;
    }
    
    public void setDriveDate(String driveDate) {
        this.driveDate = driveDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
} 