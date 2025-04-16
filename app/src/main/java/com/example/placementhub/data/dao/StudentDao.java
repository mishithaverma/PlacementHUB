package com.example.placementhub.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.placementhub.data.models.Student;

import java.util.List;

/**
 * DAO for student-related database operations
 */
@Dao
public interface StudentDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Student student);
    
    @Update
    void update(Student student);
    
    @Delete
    void delete(Student student);
    
    @Query("SELECT * FROM students WHERE sapId = :sapId LIMIT 1")
    LiveData<Student> getStudentById(String sapId);
    
    @Query("SELECT * FROM students ORDER BY fullName ASC")
    LiveData<List<Student>> getAllStudents();
    
    @Query("SELECT * FROM students WHERE branch = :branch ORDER BY fullName ASC")
    LiveData<List<Student>> getStudentsByBranch(String branch);
    
    @Query("SELECT * FROM students WHERE email = :email LIMIT 1")
    LiveData<Student> getStudentByEmail(String email);
    
    @Query("SELECT * FROM students WHERE email = :email AND password = :password LIMIT 1")
    LiveData<Student> login(String email, String password);
    
    @Query("SELECT COUNT(*) FROM students")
    int getStudentCount();
} 