package com.example.placementhub.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.placementhub.data.models.Drive;
import com.example.placementhub.data.models.Feedback;
import com.example.placementhub.data.repository.DriveRepository;
import com.example.placementhub.data.repository.FeedbackRepository;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StudentViewModel extends AndroidViewModel {
    private DriveRepository driveRepository;
    private FeedbackRepository feedbackRepository;
    private MutableLiveData<List<Drive>> upcomingDrives;
    private MutableLiveData<List<Drive>> ongoingDrives;
    private MutableLiveData<List<Drive>> completedDrives;
    
    public StudentViewModel(Application application) {
        super(application);
        driveRepository = new DriveRepository(application);
        feedbackRepository = new FeedbackRepository(application);
        upcomingDrives = new MutableLiveData<>();
        ongoingDrives = new MutableLiveData<>();
        completedDrives = new MutableLiveData<>();
        
        // Load initial data
        loadDrives();
    }
    
    public LiveData<List<Drive>> getUpcomingDrives() {
        return upcomingDrives;
    }
    
    public LiveData<List<Drive>> getOngoingDrives() {
        return ongoingDrives;
    }
    
    public LiveData<List<Drive>> getCompletedDrives() {
        return completedDrives;
    }
    
    private void loadDrives() {
        // In a real app, this would fetch from a database or API
        // For now, we'll create some sample data
        List<Drive> allDrives = createSampleDrives();
        categorizeDrives(allDrives);
    }
    
    private List<Drive> createSampleDrives() {
        List<Drive> drives = new ArrayList<>();
        
        // Upcoming drive
        Drive upcoming = new Drive();
        upcoming.setCompanyName("Tech Corp");
        upcoming.setJobRole("Software Engineer");
        upcoming.setDriveDate(getDateAfterDays(5));
        upcoming.setStatus("UPCOMING");
        drives.add(upcoming);
        
        // Ongoing drive
        Drive ongoing = new Drive();
        ongoing.setCompanyName("Innovate Solutions");
        ongoing.setJobRole("Data Scientist");
        ongoing.setDriveDate(new Date());
        ongoing.setStatus("ONGOING");
        drives.add(ongoing);
        
        // Completed drive
        Drive completed = new Drive();
        completed.setCompanyName("Global Systems");
        completed.setJobRole("Product Manager");
        completed.setDriveDate(getDateAfterDays(-5));
        completed.setStatus("COMPLETED");
        drives.add(completed);
        
        return drives;
    }
    
    private Date getDateAfterDays(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }
    
    private void categorizeDrives(List<Drive> drives) {
        List<Drive> upcoming = new ArrayList<>();
        List<Drive> ongoing = new ArrayList<>();
        List<Drive> completed = new ArrayList<>();
        
        for (Drive drive : drives) {
            switch (drive.getStatus()) {
                case "UPCOMING":
                    upcoming.add(drive);
                    break;
                case "ONGOING":
                    ongoing.add(drive);
                    break;
                case "COMPLETED":
                    completed.add(drive);
                    break;
            }
        }
        
        upcomingDrives.setValue(upcoming);
        ongoingDrives.setValue(ongoing);
        completedDrives.setValue(completed);
    }
    
    public void applyForDrive(Drive drive) {
        drive.setApplied(true);
        // In a real app, this would update the database
        loadDrives(); // Refresh the lists
    }
    
    public void submitFeedback(Feedback feedback) {
        // In a real app, this would save to the database
        feedbackRepository.insertFeedback(feedback);
    }
} 