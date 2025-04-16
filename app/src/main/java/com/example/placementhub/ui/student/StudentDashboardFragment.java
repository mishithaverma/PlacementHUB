package com.example.placementhub.ui.student;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.placementhub.R;
import com.example.placementhub.data.database.AppDatabase;
import com.example.placementhub.data.models.DriveInfo;
import com.example.placementhub.data.models.Student;
import com.example.placementhub.viewmodels.StudentViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

/**
 * Main dashboard for students to view and interact with their drives
 */
public class StudentDashboardFragment extends Fragment implements DriveAdapter.DriveListener {
    
    private static final String TAG = "StudentDashboardFrag";
    private static final String ARG_STUDENT_SAP_ID = "student_sap_id";
    
    private String studentSapId;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private View emptyView;
    private AppDatabase db;
    private Student currentStudent;
    private StudentViewModel viewModel;
    
    public static StudentDashboardFragment newInstance(String studentSapId) {
        StudentDashboardFragment fragment = new StudentDashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STUDENT_SAP_ID, studentSapId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize studentSapId to prevent NullPointerException
        studentSapId = "";
        
        if (getArguments() != null) {
            studentSapId = getArguments().getString(ARG_STUDENT_SAP_ID, "");
        }
        
        // For demo/testing purposes, use a default SAP ID if none is provided
        if (studentSapId.isEmpty()) {
            studentSapId = "60004200001"; // Sample student SAP ID
        }
        
        // Get database instance
        db = AppDatabase.getDatabase(requireContext());
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_dashboard, container, false);
        
        // Initialize views
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        viewPager = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.tabs);
        emptyView = view.findViewById(R.id.empty_view);
        
        // Set up toolbar
        toolbar.setTitle("Placement Drives");
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(StudentViewModel.class);
        
        // Setup RecyclerViews
        setupRecyclerViews(view);
        
        // Load student information
        loadStudentInfo();
        
        // Set up ViewPager and TabLayout
        setupViewPager();
        
        // Set up empty view
        checkIfDrivesExist();
    }
    
    private void setupRecyclerViews(View view) {
        // This method is no longer needed with the tabbed interface 
        // which uses fragment-based recycler views
        // Each tab will create its own DrivesFragment which will set up its own RecyclerView
    }
    
    private void loadStudentInfo() {
        try {
            // Load current student information from database
            db.studentDao().getStudentById(studentSapId).observe(getViewLifecycleOwner(), student -> {
                if (student != null) {
                    currentStudent = student;
                    updateUIWithStudentInfo();
                } else {
                    Log.e(TAG, "Student not found with SAP ID: " + studentSapId);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading student info: " + e.getMessage(), e);
        }
    }
    
    private void updateUIWithStudentInfo() {
        // Update toolbar title with student name
        if (currentStudent != null) {
            Toolbar toolbar = getView().findViewById(R.id.toolbar);
            toolbar.setTitle("Drives for " + currentStudent.getFullName());
        }
    }
    
    private void setupViewPager() {
        // Set up the adapter for the ViewPager
        viewPager.setAdapter(new DrivesTabAdapter(this));
        
        // Connect the TabLayout with the ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Upcoming");
                    break;
                case 1:
                    tab.setText("Ongoing");
                    break;
                case 2:
                    tab.setText("Done");
                    break;
            }
        }).attach();
    }
    
    private void checkIfDrivesExist() {
        // Check if the student has any drives assigned
        try {
            db.studentDriveDao().getDriveCountForStudent(studentSapId).observe(getViewLifecycleOwner(), count -> {
                if (count > 0) {
                    // Student has drives, show the ViewPager
                    viewPager.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                } else {
                    // No drives, show empty state
                    viewPager.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error checking if drives exist: " + e.getMessage(), e);
            
            // On error, default to showing the ViewPager
            viewPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
    
    /**
     * Adapter for the ViewPager to manage the three tab fragments
     */
    private class DrivesTabAdapter extends FragmentStateAdapter {
        
        public DrivesTabAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }
        
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return DrivesFragment.newInstance("upcoming", studentSapId);
                case 1:
                    return DrivesFragment.newInstance("ongoing", studentSapId);
                case 2:
                    return DrivesFragment.newInstance("done", studentSapId);
                default:
                    return DrivesFragment.newInstance("upcoming", studentSapId);
            }
        }
        
        @Override
        public int getItemCount() {
            return 3; // Upcoming, Ongoing, Done
        }
    }
    
    /**
     * DriveAdapter.DriveListener interface implementation
     */
    @Override
    public void onViewDetailsClicked(DriveInfo drive) {
        if (drive.getStatus().equals("done")) {
            showFeedbackDialog(drive);
        } else {
            showDriveDetailsDialog(drive);
        }
    }
    
    @Override
    public void onApplyClicked(DriveInfo drive) {
        // Show confirmation dialog
        new AlertDialog.Builder(requireContext())
                .setTitle("Apply for Drive")
                .setMessage("Are you sure you want to apply for " + drive.getCompanyName() + " drive?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Apply for the drive
                    new Thread(() -> {
                        try {
                            db.studentDriveDao().markDriveAsApplied(studentSapId, drive.getId());
                            
                            // Show confirmation toast on the main thread
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    android.widget.Toast.makeText(requireContext(),
                                            "Successfully applied for " + drive.getCompanyName() + " drive",
                                            android.widget.Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error applying for drive: " + e.getMessage(), e);
                            
                            // Show error toast on the main thread
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    android.widget.Toast.makeText(requireContext(),
                                            "Failed to apply: " + e.getMessage(),
                                            android.widget.Toast.LENGTH_SHORT).show();
                                });
                            }
                        }
                    }).start();
                })
                .setNegativeButton("No", null)
                .show();
    }
    
    private void showDriveDetailsDialog(DriveInfo drive) {
        // Show a dialog with drive details
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(drive.getCompanyName() + " - " + drive.getJobTitle())
            .setMessage("Location: " + drive.getLocation() + "\n\n" +
                    "Package: " + drive.getPackageOffered() + "\n\n" +
                    "Type: " + drive.getJobType() + "\n\n" +
                    "Drive Date: " + drive.getDriveDate() + "\n\n" +
                    "Interview Mode: " + drive.getInterviewMode() + "\n\n" +
                    "Description: " + drive.getJobDescription())
            .setPositiveButton("OK", null)
            .show();
    }
    
    private void showFeedbackDialog(DriveInfo drive) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_feedback_form);
        
        RatingBar ratingBar = dialog.findViewById(R.id.rating_bar);
        RadioGroup roleCommunicationGroup = dialog.findViewById(R.id.role_communication_group);
        EditText issuesFaced = dialog.findViewById(R.id.issues_faced);
        EditText suggestions = dialog.findViewById(R.id.suggestions);
        RadioGroup futureParticipationGroup = dialog.findViewById(R.id.future_participation_group);
        Button submitButton = dialog.findViewById(R.id.btn_submit_feedback);
        
        submitButton.setOnClickListener(v -> {
            // Create feedback object
            String feedback = "Rating: " + (int) ratingBar.getRating() + 
                    "\nRole Communication: " + (roleCommunicationGroup.getCheckedRadioButtonId() == R.id.role_yes ? "Yes" : "No") +
                    "\nIssues: " + issuesFaced.getText().toString() +
                    "\nSuggestions: " + suggestions.getText().toString() +
                    "\nFuture Participation: " + (futureParticipationGroup.getCheckedRadioButtonId() == R.id.participation_yes ? "Yes" : "No");
            
            // Show confirmation
            Toast.makeText(requireContext(), "Feedback submitted", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        
        dialog.show();
    }
} 