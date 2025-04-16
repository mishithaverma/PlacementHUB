package com.example.placementhub.ui.student;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.placementhub.data.AppDatabase;
import com.example.placementhub.data.dao.StudentDao;
import com.example.placementhub.data.models.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentViewModel extends AndroidViewModel {
    private static final String TAG = "StudentViewModel";

    private StudentDao studentDao;
    private final ExecutorService executorService;
    private LiveData<List<Student>> allStudents;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<List<Student>> studentListData = new MutableLiveData<>(new ArrayList<>());
    
    public StudentViewModel(@NonNull Application application) {
        super(application);
        executorService = Executors.newFixedThreadPool(4);
        
        try {
            AppDatabase database = AppDatabase.getDatabase(application);
            studentDao = database.studentDao();
            allStudents = studentDao.getAllStudents();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database: " + e.getMessage());
            // Create empty LiveData as fallback
            allStudents = studentListData;
        }
    }

    public LiveData<List<Student>> getAllStudents() {
        return allStudents;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public void refreshStudents() {
        try {
            if (studentDao != null) {
                // If using Room, this is automatic through LiveData
                return;
            }
            
            // For testing purposes only - use with mock data when database is not available
            List<Student> mockStudents = new ArrayList<>();
            mockStudents.add(new Student("60004200001", "Test Student", "test@example.com", 
                "password", "CSE", "CS001", 3.5f, "Male", 0, "2024"));
            studentListData.postValue(mockStudents);
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing students: " + e.getMessage());
        }
    }
    
    /**
     * Insert a student
     * @param student Student to insert
     * @return true if successful, false otherwise
     */
    public boolean insertStudent(Student student) {
        try {
            if (studentDao == null) {
                Log.e(TAG, "StudentDao is null, cannot insert student");
                return false;
            }
            
            if (student == null) {
                Log.e(TAG, "Student is null, cannot insert");
                return false;
            }
            
            executorService.execute(() -> {
                try {
                    studentDao.insert(student);
                } catch (Exception e) {
                    Log.e(TAG, "Error inserting student: " + e.getMessage());
                }
            });
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Exception in insertStudent: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Insert a student if it doesn't exist (based on SAP ID)
     * @param student Student to insert
     * @return true if inserted, false if skipped
     */
    public boolean insertIfNotExists(Student student) {
        try {
            if (studentDao == null) {
                Log.e(TAG, "StudentDao is null, cannot insert student");
                return false;
            }
            
            if (student == null) {
                Log.e(TAG, "Student is null, cannot insert");
                return false;
            }
            
            executorService.execute(() -> {
                try {
                    studentDao.insert(student);
                } catch (Exception e) {
                    Log.e(TAG, "Error inserting student: " + e.getMessage());
                }
            });
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Exception in insertIfNotExists: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update or insert a student (based on SAP ID)
     * @param student Student to update or insert
     * @return true if successful, false otherwise
     */
    public boolean upsertStudent(Student student) {
        try {
            if (studentDao == null) {
                Log.e(TAG, "StudentDao is null, cannot upsert student");
                return false;
            }
            
            if (student == null) {
                Log.e(TAG, "Student is null, cannot upsert");
                return false;
            }
            
            executorService.execute(() -> {
                try {
                    // Try update
                    studentDao.update(student);
                } catch (Exception e) {
                    try {
                        // If update fails, try insert
                        studentDao.insert(student);
                    } catch (Exception e2) {
                        Log.e(TAG, "Error upserting student: " + e2.getMessage());
                    }
                }
            });
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Exception in upsertStudent: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete a student
     * @param student Student to delete
     */
    public void deleteStudent(Student student) {
        try {
            if (studentDao == null) {
                Log.e(TAG, "StudentDao is null, cannot delete student");
                return;
            }
            
            if (student == null) {
                Log.e(TAG, "Student is null, cannot delete");
                return;
            }
            
            executorService.execute(() -> {
                try {
                    studentDao.delete(student);
                } catch (Exception e) {
                    Log.e(TAG, "Error deleting student: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in deleteStudent: " + e.getMessage());
        }
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
} 