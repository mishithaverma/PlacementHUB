package com.example.placementhub.ui.student;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.placementhub.R;
import com.example.placementhub.data.models.DriveInfo;
import com.example.placementhub.data.models.StudentDrive;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DriveAdapter extends RecyclerView.Adapter<DriveAdapter.DriveViewHolder> {
    
    private List<DriveInfo> drives;
    private final Context context;
    private final DriveListener listener;
    private final Map<String, StudentDrive> studentDriveMap;
    private final String driveType; // "upcoming", "ongoing", or "done"
    
    public DriveAdapter(Context context, List<DriveInfo> drives, Map<String, StudentDrive> studentDriveMap, 
                        DriveListener listener, String driveType) {
        this.context = context;
        this.drives = drives != null ? drives : new ArrayList<>();
        this.studentDriveMap = studentDriveMap;
        this.listener = listener;
        this.driveType = driveType;
    }
    
    @NonNull
    @Override
    public DriveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_drive, parent, false);
        return new DriveViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull DriveViewHolder holder, int position) {
        DriveInfo drive = drives.get(position);
        holder.bind(drive);
    }
    
    @Override
    public int getItemCount() {
        return drives.size();
    }
    
    public void updateDrives(List<DriveInfo> newDrives) {
        this.drives = newDrives != null ? newDrives : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public class DriveViewHolder extends RecyclerView.ViewHolder {
        private final TextView companyName;
        private final TextView jobTitle;
        private final TextView packageView;
        private final TextView location;
        private final TextView jobType;
        private final TextView driveDate;
        private final TextView interviewMode;
        private final Button viewDetailsButton;
        private final Button applyButton;
        private final TextView appliedTag;
        private final LinearLayout buttonContainer;
        
        public DriveViewHolder(@NonNull View itemView) {
            super(itemView);
            
            companyName = itemView.findViewById(R.id.tv_company_name);
            jobTitle = itemView.findViewById(R.id.tv_job_title);
            packageView = itemView.findViewById(R.id.tv_package);
            location = itemView.findViewById(R.id.tv_location);
            jobType = itemView.findViewById(R.id.tv_job_type);
            driveDate = itemView.findViewById(R.id.tv_drive_date);
            interviewMode = itemView.findViewById(R.id.tv_interview_mode);
            viewDetailsButton = itemView.findViewById(R.id.btn_view_details);
            applyButton = itemView.findViewById(R.id.btn_apply);
            appliedTag = itemView.findViewById(R.id.tv_applied_tag);
            buttonContainer = itemView.findViewById(R.id.button_container);
        }
        
        public void bind(DriveInfo drive) {
            companyName.setText(drive.getCompanyName());
            jobTitle.setText(drive.getJobTitle());
            
            // Set package info if available
            if (drive.getPackageOffered() != null && !drive.getPackageOffered().isEmpty()) {
                packageView.setText(drive.getPackageOffered());
                packageView.setVisibility(View.VISIBLE);
            } else {
                packageView.setVisibility(View.GONE);
            }
            
            // Set location
            location.setText(drive.getLocation());
            
            // Set job type
            jobType.setText(drive.getJobType());
            
            // Format and set date
            driveDate.setText(formatDate(drive.getDriveDate()));
            
            // Set interview mode
            interviewMode.setText(drive.getInterviewMode() + " Interview");
            
            // Set view details button click listener
            viewDetailsButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewDetailsClicked(drive);
                }
            });
            
            // Configure apply button and applied tag based on drive status and application status
            StudentDrive studentDrive = studentDriveMap.get(drive.getId());
            boolean isApplied = studentDrive != null && studentDrive.isApplied();
            
            if (driveType.equals("upcoming") && !isApplied) {
                // For upcoming drives that haven't been applied to, show apply button
                buttonContainer.setVisibility(View.VISIBLE);
                applyButton.setVisibility(View.VISIBLE);
                appliedTag.setVisibility(View.GONE);
                
                applyButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onApplyClicked(drive);
                    }
                });
            } else if (isApplied) {
                // For applied drives, show applied tag and hide apply button
                buttonContainer.setVisibility(View.VISIBLE);
                applyButton.setVisibility(View.GONE);
                appliedTag.setVisibility(View.VISIBLE);
            } else if (driveType.equals("done")) {
                // For past drives, hide both apply button and applied tag if not applied
                buttonContainer.setVisibility(View.VISIBLE);
                applyButton.setVisibility(View.GONE);
                appliedTag.setVisibility(View.GONE);
            } else {
                // For ongoing drives, show view details only
                buttonContainer.setVisibility(View.VISIBLE);
                applyButton.setVisibility(View.GONE);
                appliedTag.setVisibility(View.GONE);
            }
        }
        
        private String formatDate(String inputDate) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
                Date date = inputFormat.parse(inputDate);
                return date != null ? outputFormat.format(date) : inputDate;
            } catch (ParseException e) {
                return inputDate;
            }
        }
    }
    
    public interface DriveListener {
        void onViewDetailsClicked(DriveInfo drive);
        void onApplyClicked(DriveInfo drive);
    }
} 