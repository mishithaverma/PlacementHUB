package com.example.placementhub.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "drives")
public class Drive {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String companyName;
    private String jobRole;
    private String jobDescription;
    private Date driveDate;
    private String location;
    private String eligibilityCriteria;
    private String packageDetails;
    private String status; // UPCOMING, ONGOING, COMPLETED
    private String documentUrl; // URL to JD/brochure
    private boolean isApplied;
    
    // Default constructor
    public Drive() {}
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getJobRole() {
        return jobRole;
    }
    
    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }
    
    public String getJobDescription() {
        return jobDescription;
    }
    
    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
    
    public Date getDriveDate() {
        return driveDate;
    }
    
    public void setDriveDate(Date driveDate) {
        this.driveDate = driveDate;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getEligibilityCriteria() {
        return eligibilityCriteria;
    }
    
    public void setEligibilityCriteria(String eligibilityCriteria) {
        this.eligibilityCriteria = eligibilityCriteria;
    }
    
    public String getPackageDetails() {
        return packageDetails;
    }
    
    public void setPackageDetails(String packageDetails) {
        this.packageDetails = packageDetails;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDocumentUrl() {
        return documentUrl;
    }
    
    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }
    
    public boolean isApplied() {
        return isApplied;
    }
    
    public void setApplied(boolean applied) {
        isApplied = applied;
    }
} 