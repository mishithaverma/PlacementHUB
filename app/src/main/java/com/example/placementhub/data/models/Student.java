package com.example.placementhub.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "students")
public class Student {
    
    @PrimaryKey
    @NonNull
    private String sapId;
    
    private String fullName;
    private String email;
    private String password;
    private String branch;
    private String rollNumber;
    private float cgpa;
    private String gender;
    private int backlogs;
    private String batch;
    
    // This field is used by UI but not stored in database
    @Ignore
    private boolean isSelected;
    
    // Default constructor required by Room
    public Student() {
        this.isSelected = false;
    }
    
    @Ignore
    public Student(@NonNull String sapId, String fullName, String email, String password, String branch, 
                   String rollNumber, float cgpa, String gender, int backlogs, String batch) {
        this.sapId = sapId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.branch = branch;
        this.rollNumber = rollNumber;
        this.cgpa = cgpa;
        this.gender = gender;
        this.backlogs = backlogs;
        this.batch = batch;
        this.isSelected = false;
    }
    
    @NonNull
    public String getSapId() {
        return sapId;
    }
    
    public void setSapId(@NonNull String sapId) {
        this.sapId = sapId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getBranch() {
        return branch;
    }
    
    public void setBranch(String branch) {
        this.branch = branch;
    }
    
    public String getRollNumber() {
        return rollNumber;
    }
    
    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }
    
    public float getCgpa() {
        return cgpa;
    }
    
    public void setCgpa(float cgpa) {
        this.cgpa = cgpa;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public int getBacklogs() {
        return backlogs;
    }
    
    public void setBacklogs(int backlogs) {
        this.backlogs = backlogs;
    }
    
    public String getBatch() {
        return batch;
    }
    
    public void setBatch(String batch) {
        this.batch = batch;
    }
    
    public boolean isSelected() {
        return isSelected;
    }
    
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
} 