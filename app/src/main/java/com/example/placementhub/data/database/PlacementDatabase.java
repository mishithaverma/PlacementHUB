package com.example.placementhub.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.placementhub.data.models.Drive;
import com.example.placementhub.data.models.Feedback;

@Database(entities = {Drive.class, Feedback.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class PlacementDatabase extends RoomDatabase {
    private static PlacementDatabase instance;
    
    public static synchronized PlacementDatabase getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                PlacementDatabase.class,
                "placement_database"
            ).build();
        }
        return instance;
    }
    
    public abstract DriveDao driveDao();
    public abstract FeedbackDao feedbackDao();
} 