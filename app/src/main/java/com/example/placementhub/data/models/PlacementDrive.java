package com.example.placementhub.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "placement_drives")
public class PlacementDrive {
    @PrimaryKey
    @NonNull
    private String driveId;

    @NonNull
    private String companyName;

    @NonNull
    private String jobRole;

    @NonNull
    private String description;

    private String requirements;
    private String location;
    private double packageAmount;
    private Date deadline;
    private String status;
    private String driveType;
    private int totalPositions;
    private int positionsFilled;

    // Constructor
    public PlacementDrive(@NonNull String driveId, @NonNull String companyName, 
                         @NonNull String jobRole, @NonNull String description,
                         String requirements, String location, double packageAmount,
                         Date deadline, String status, String driveType,
                         int totalPositions, int positionsFilled) {
        this.driveId = driveId;
        this.companyName = companyName;
        this.jobRole = jobRole;
        this.description = description;
        this.requirements = requirements;
        this.location = location;
        this.packageAmount = packageAmount;
        this.deadline = deadline;
        this.status = status;
        this.driveType = driveType;
        this.totalPositions = totalPositions;
        this.positionsFilled = positionsFilled;
    }

    // Getters and Setters
    @NonNull
    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(@NonNull String driveId) {
        this.driveId = driveId;
    }

    @NonNull
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(@NonNull String companyName) {
        this.companyName = companyName;
    }

    @NonNull
    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(@NonNull String jobRole) {
        this.jobRole = jobRole;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getPackageAmount() {
        return packageAmount;
    }

    public void setPackageAmount(double packageAmount) {
        this.packageAmount = packageAmount;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDriveType() {
        return driveType;
    }

    public void setDriveType(String driveType) {
        this.driveType = driveType;
    }

    public int getTotalPositions() {
        return totalPositions;
    }

    public void setTotalPositions(int totalPositions) {
        this.totalPositions = totalPositions;
    }

    public int getPositionsFilled() {
        return positionsFilled;
    }

    public void setPositionsFilled(int positionsFilled) {
        this.positionsFilled = positionsFilled;
    }
} 