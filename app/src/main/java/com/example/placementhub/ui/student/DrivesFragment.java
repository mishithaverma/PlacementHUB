package com.example.placementhub.ui.student;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placementhub.R;
import com.example.placementhub.data.database.AppDatabase;
import com.example.placementhub.data.models.DriveInfo;
import com.example.placementhub.data.models.StudentDrive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment to display a list of drives by status (upcoming, ongoing, done)
 */
public class DrivesFragment extends Fragment implements DriveAdapter.DriveListener {
    
    private static final String TAG = "DrivesFragment";
    private static final String ARG_DRIVE_TYPE = "drive_type";
    private static final String ARG_STUDENT_SAP_ID = "student_sap_id";
    
    private String driveType;
    private String studentSapId;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private DriveAdapter adapter;
    private AppDatabase db;
    private Map<String, StudentDrive> studentDriveMap = new HashMap<>();
    
    public static DrivesFragment newInstance(String driveType, String studentSapId) {
        DrivesFragment fragment = new DrivesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DRIVE_TYPE, driveType);
        args.putString(ARG_STUDENT_SAP_ID, studentSapId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getArguments() != null) {
            driveType = getArguments().getString(ARG_DRIVE_TYPE, "upcoming");
            studentSapId = getArguments().getString(ARG_STUDENT_SAP_ID, "");
        }
        
        // Get database instance
        db = AppDatabase.getDatabase(requireContext());
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drives_list, container, false);
        
        recyclerView = view.findViewById(R.id.drives_recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new DriveAdapter(requireContext(), null, studentDriveMap, this, driveType);
        recyclerView.setAdapter(adapter);
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Load student-drive relationships
        db.studentDriveDao().getStudentDrives(studentSapId).observe(getViewLifecycleOwner(), studentDrives -> {
            // Build a map of drive ID to StudentDrive for quick lookup
            studentDriveMap.clear();
            if (studentDrives != null) {
                for (StudentDrive sd : studentDrives) {
                    studentDriveMap.put(sd.getDriveId(), sd);
                }
            }
            
            // Load drives based on type
            loadDrives();
        });
    }
    
    private void loadDrives() {
        // Load drives based on drive type
        switch (driveType) {
            case "upcoming":
                db.driveDao().getUpcomingDrivesForStudent(studentSapId)
                        .observe(getViewLifecycleOwner(), this::updateDrivesList);
                break;
            case "ongoing":
                db.driveDao().getOngoingDrivesForStudent(studentSapId)
                        .observe(getViewLifecycleOwner(), this::updateDrivesList);
                break;
            case "done":
                db.driveDao().getCompletedDrivesForStudent(studentSapId)
                        .observe(getViewLifecycleOwner(), this::updateDrivesList);
                break;
            default:
                Log.e(TAG, "Unknown drive type: " + driveType);
                break;
        }
    }
    
    private void updateDrivesList(List<DriveInfo> drives) {
        if (drives != null && !drives.isEmpty()) {
            adapter.updateDrives(drives);
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public void onViewDetailsClicked(DriveInfo drive) {
        // Show drive details dialog or navigate to details screen
        showDriveDetailsDialog(drive);
    }
    
    @Override
    public void onApplyClicked(DriveInfo drive) {
        // Apply for the drive
        applyForDrive(drive);
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
    
    private void applyForDrive(DriveInfo drive) {
        // Update the database to mark the drive as applied
        new Thread(() -> {
            try {
                db.studentDriveDao().markDriveAsApplied(studentSapId, drive.getId());
                
                // Reload the UI on the main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Update the local map and notify the adapter
                        StudentDrive studentDrive = studentDriveMap.get(drive.getId());
                        if (studentDrive != null) {
                            studentDrive.setApplied(true);
                            adapter.notifyDataSetChanged();
                        }
                        
                        // Show confirmation toast
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
    }
} 