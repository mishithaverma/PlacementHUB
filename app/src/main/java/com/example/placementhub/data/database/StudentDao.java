package com.example.placementhub.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.placementhub.data.models.Student;
import java.util.List;

@Dao
public interface StudentDao {
    @Insert
    void insert(Student student);

    @Update
    void update(Student student);

    @Delete
    void delete(Student student);

    @Query("SELECT * FROM students")
    LiveData<List<Student>> getAllStudents();

    @Query("SELECT * FROM students WHERE sapId = :sapId")
    LiveData<Student> getStudentById(String sapId);

    @Query("SELECT * FROM students WHERE email = :email LIMIT 1")
    LiveData<Student> getStudentByEmail(String email);

    @Query("SELECT * FROM students WHERE email = :email AND password = :password LIMIT 1")
    LiveData<Student> login(String email, String password);

    @Query("SELECT * FROM students WHERE branch = :branch")
    LiveData<List<Student>> getStudentsByBranch(String branch);

    @Query("SELECT * FROM students WHERE batch = :batch")
    LiveData<List<Student>> getStudentsByBatch(String batch);

    @Query("SELECT * FROM students WHERE cgpa >= :minCgpa")
    LiveData<List<Student>> getStudentsByMinCgpa(float minCgpa);

    @Query("SELECT * FROM students WHERE backlogs <= :maxBacklogs")
    LiveData<List<Student>> getStudentsByMaxBacklogs(int maxBacklogs);
} 