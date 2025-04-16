package com.example.placementcell.models;

public class Student {
    private String fullName;
    private String branch;
    private String rollNumber;
    private String sapId;
    private double cgpa;
    private String gender;
    private int backlogs;
    private String batch;
    private String email;
    private boolean isSelected;

    public Student() {
        // Default constructor required for Firebase
    }

    public Student(String fullName, String branch, String rollNumber, String sapId, 
                  double cgpa, String gender, int backlogs, String batch, String email) {
        this.fullName = fullName;
        this.branch = branch;
        this.rollNumber = rollNumber;
        this.sapId = sapId;
        this.cgpa = cgpa;
        this.gender = gender;
        this.backlogs = backlogs;
        this.batch = batch;
        this.email = email;
        this.isSelected = false;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getSapId() {
        return sapId;
    }

    public void setSapId(String sapId) {
        this.sapId = sapId;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "Student{" +
                "fullName='" + fullName + '\'' +
                ", branch='" + branch + '\'' +
                ", rollNumber='" + rollNumber + '\'' +
                ", sapId='" + sapId + '\'' +
                ", cgpa=" + cgpa +
                ", gender='" + gender + '\'' +
                ", backlogs=" + backlogs +
                ", batch='" + batch + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
} 