package com.example.placementhub.data.database;

import android.content.Context;
import android.util.Log;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.placementhub.data.models.Student;
import com.example.placementhub.data.models.DriveInfo;
import com.example.placementhub.data.models.StudentDrive;
import com.example.placementhub.data.dao.StudentDao;
import com.example.placementhub.data.dao.DriveDao;
import com.example.placementhub.data.dao.StudentDriveDao;

@Database(entities = {
        Student.class, 
        DriveInfo.class,
        StudentDrive.class
    },
    version = 2, 
    exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = "AppDatabase";
    private static volatile AppDatabase INSTANCE;

    public abstract StudentDao studentDao();
    public abstract DriveDao driveDao();
    public abstract StudentDriveDao studentDriveDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    try {
                        Log.d(TAG, "Creating database instance");
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                AppDatabase.class, "placement_hub_database")
                                .fallbackToDestructiveMigration()
                                .build();
                        Log.d(TAG, "Database instance created successfully");
                    } catch (Exception e) {
                        Log.e(TAG, "Error creating database: " + e.getMessage(), e);
                        throw new RuntimeException("Failed to create database", e);
                    }
                }
            }
        }
        return INSTANCE;
    }
} 