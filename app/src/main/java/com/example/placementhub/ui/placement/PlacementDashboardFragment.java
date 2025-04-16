package com.example.placementhub.ui.placement;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.placementhub.R;
import com.example.placementhub.data.database.AppDatabase;
import com.example.placementhub.data.models.Student;
import com.example.placementhub.data.models.DriveInfo;
import com.example.placementhub.data.models.StudentDrive;
import com.example.placementhub.utils.NotificationWorker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PlacementDashboardFragment extends Fragment implements StudentTableAdapter.OnStudentItemClickListener {
    private static final String TAG = "PlacementDashboardFrag";
    
    private AppDatabase db;
    private RecyclerView studentsTable;
    private TextView emptyView;
    private EditText searchEditText;
    private Button companyInfoButton;
    private Button btnStudentList;
    private Button applyFiltersButton;
    private androidx.appcompat.widget.AppCompatSpinner branchFilter;
    private TextInputEditText rollMinFilter;
    private TextInputEditText rollMaxFilter;
    private androidx.appcompat.widget.AppCompatSpinner cgpaFilter;
    private androidx.appcompat.widget.AppCompatSpinner genderFilter;
    private TextInputEditText backlogsFilter;
    private CheckBox selectAllCheckbox;
    private StudentTableAdapter studentAdapter;
    private List<Student> allStudents = new ArrayList<>();
    private List<Student> filteredStudents = new ArrayList<>();
    
    // Branch options
    private final String[] branchOptions = {
        "All Branches",
        "BTech Computer Engineering",
        "BTech Information Technology",
        "BTech CSE (Cyber Security)",
        "BTech Data Science",
        "BTech Artificial Intelligence",
        "BTech Civil Engineering",
        "BTech Mechatronics Engineering",
        "BTech Electrical and Telecommunication Engineering",
        "BTech Computer Science and Business Systems",
        "BTech Mechanical"
    };
    
    // CGPA options
    private final String[] cgpaOptions = {
        "Any CGPA", "1.0+", "1.5+", "2.0+", "2.5+", "3.0+", "3.5+", "3.8+"
    };
    
    // Gender options
    private final String[] genderOptions = {"All", "Boy", "Girl", "Other"};

    private Executor executor = Executors.newSingleThreadExecutor();
    
    // Variables at class level
    private Uri selectedCsvUri;
    private ActivityResultLauncher<String> csvPickerLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_placement_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");
        
        try {
            // Initialize views
            studentsTable = view.findViewById(R.id.students_table);
            emptyView = view.findViewById(R.id.empty_view);
            searchEditText = view.findViewById(R.id.search_edit_text);
            companyInfoButton = view.findViewById(R.id.company_info_button);
            btnStudentList = view.findViewById(R.id.btn_student_list);
            applyFiltersButton = view.findViewById(R.id.apply_filters_button);
            selectAllCheckbox = view.findViewById(R.id.select_all_checkbox);
            
            // Initialize filter views
            branchFilter = view.findViewById(R.id.branch_filter);
            rollMinFilter = view.findViewById(R.id.roll_min_filter);
            rollMaxFilter = view.findViewById(R.id.roll_max_filter);
            cgpaFilter = view.findViewById(R.id.cgpa_filter);
            genderFilter = view.findViewById(R.id.gender_filter);
            backlogsFilter = view.findViewById(R.id.backlogs_filter);
            
            // Set up dropdown adapters
            setupDropdowns();
            
            // Set up RecyclerView
            studentsTable.setLayoutManager(new LinearLayoutManager(requireContext()));
            studentAdapter = new StudentTableAdapter(this);
            studentsTable.setAdapter(studentAdapter);
            
            // Set up select all functionality
            selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    studentAdapter.selectAll();
                } else {
                    studentAdapter.clearSelection();
                }
            });
            
            // Set up search functionality
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    filterStudents(s.toString());
                }
            });
            
            // Initialize database and load students
            try {
                db = AppDatabase.getDatabase(requireContext());
                loadStudents();
            } catch (Exception e) {
                Log.e(TAG, "Error loading from database, using sample data instead", e);
                loadSampleData();
            }
            
            // Set click listeners
            applyFiltersButton.setOnClickListener(v -> applyFilters());
            companyInfoButton.setOnClickListener(v -> showCompanyInfoDialog());
            
            // Use a direct approach to handle StudentListFragment
            btnStudentList.setOnClickListener(v -> {
                try {
                    Toast.makeText(requireContext(), "Opening student import screen...", Toast.LENGTH_SHORT).show();
                    
                    // Instead of replacing the entire fragment, show a dialog with the CSV functionality
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Student CSV Import");
                    
                    // Inflate a simplified layout
                    View csvView = getLayoutInflater().inflate(R.layout.fragment_student_list, null);
                    builder.setView(csvView);
                    
                    // Create and show the dialog
                    AlertDialog dialog = builder.create();
                    
                    // Set up UI elements
                    Button btnSelectCsv = csvView.findViewById(R.id.btn_select_csv);
                    Button btnUploadCsv = csvView.findViewById(R.id.btn_upload_csv);
                    Button btnDownloadSample = csvView.findViewById(R.id.btn_download_sample);
                    ImageButton btnCsvInfo = csvView.findViewById(R.id.btn_csv_info);
                    TextView txtSelectedFile = csvView.findViewById(R.id.txt_selected_file);
                    CheckBox chkOverwriteDuplicates = csvView.findViewById(R.id.chk_overwrite_duplicates);
                    ProgressBar progressUpload = csvView.findViewById(R.id.progress_upload);
                    TextView txtUploadStatus = csvView.findViewById(R.id.txt_upload_status);
                    
                    // Select CSV button - open file picker to select actual CSV file
                    btnSelectCsv.setOnClickListener(selectBtn -> {
                        try {
                            // Launch file picker for CSV file selection
                            csvPickerLauncher.launch("text/csv");
                        } catch (Exception e) {
                            Log.e(TAG, "Error launching file picker", e);
                            Toast.makeText(requireContext(), 
                                "Error selecting file: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                                
                            // Fallback to generic mime type if CSV specific type fails
                            try {
                                csvPickerLauncher.launch("*/*");
                            } catch (Exception e2) {
                                Log.e(TAG, "Fallback file picker also failed", e2);
                            }
                        }
                    });
                    
                    // Upload CSV button
                    btnUploadCsv.setOnClickListener(uploadBtn -> {
                        if (selectedCsvUri == null) {
                            Toast.makeText(requireContext(), "Please select a file first", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        // Show progress
                        progressUpload.setVisibility(View.VISIBLE);
                        progressUpload.setIndeterminate(true);
                        txtUploadStatus.setVisibility(View.VISIBLE);
                        txtUploadStatus.setText("Processing CSV...");
                        
                        // Process the CSV file in background thread
                        new Thread(() -> {
                            try {
                                // Get InputStream from URI
                                InputStream inputStream = requireContext().getContentResolver().openInputStream(selectedCsvUri);
                                
                                // Parse CSV and process students
                                List<Student> parsedStudents = parseCSV(inputStream);
                                
                                // Sleep for a short time to show progress (remove in production)
                                Thread.sleep(1000);
                                
                                // Update UI on main thread
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        try {
                                            // Update progress and status
                                            progressUpload.setIndeterminate(false);
                                            progressUpload.setProgress(100);
                                            txtUploadStatus.setText("Successfully imported " + parsedStudents.size() + " students");
                                            
                                            // Enable buttons again
                                            btnSelectCsv.setEnabled(true);
                                            btnUploadCsv.setEnabled(true);
                                            
                                            // Show success message
                                            Toast.makeText(requireContext(), 
                                                "Successfully imported " + parsedStudents.size() + " students", 
                                                Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            Log.e(TAG, "Error updating UI after CSV import", e);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing CSV file", e);
                                
                                // Update UI on main thread
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        progressUpload.setVisibility(View.GONE);
                                        txtUploadStatus.setText("Error: " + e.getMessage());
                                        
                                        // Enable buttons again
                                        btnSelectCsv.setEnabled(true);
                                        btnUploadCsv.setEnabled(true);
                                        
                                        Toast.makeText(requireContext(), 
                                            "Error processing CSV: " + e.getMessage(), 
                                            Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }
                        }).start();
                    });
                    
                    // Download sample button
                    btnDownloadSample.setOnClickListener(downloadBtn -> {
                        // Show sample download in progress
                        Toast.makeText(requireContext(), 
                            "Downloading sample CSV template...", 
                            Toast.LENGTH_SHORT).show();
                        
                        // Simulate download delay
                        new Handler().postDelayed(() -> {
                            // Show sample content
                            new AlertDialog.Builder(requireContext())
                                .setTitle("Sample CSV Format")
                                .setMessage("Sample CSV saved to Downloads folder\n\n" +
                                        "Format:\n" +
                                        "sapId,fullName,email,password,branch,rollNumber,cgpa,gender,backlogs,batch\n" +
                                        "60004200001,John Doe,john.doe@example.com,pass123,CSE,CS001,8.5,Male,0,2023\n" +
                                        "60004200002,Jane Smith,jane.smith@example.com,pass456,IT,IT002,9.0,Female,0,2023")
                                .setPositiveButton("OK", null)
                                .show();
                        }, 1000);  // 1 second delay to simulate download
                    });
                    
                    // Info button
                    btnCsvInfo.setOnClickListener(infoBtn -> {
                        new AlertDialog.Builder(requireContext())
                            .setTitle("CSV Format Requirements")
                            .setMessage("Your CSV file must have the following headers:\n\n" +
                                "sapId,fullName,email,password,branch,rollNumber,cgpa,gender,backlogs,batch\n\n" +
                                "Required fields: sapId, fullName, email, password, branch, rollNumber\n" +
                                "Optional fields: cgpa, gender, backlogs, batch\n\n" +
                                "Data validation:\n" +
                                "• SAP ID must be unique\n" +
                                "• Email must be unique and valid format\n" +
                                "• CGPA must be a numeric value between 0-10\n" +
                                "• Backlogs must be a non-negative integer\n" +
                                "• Batch must be a valid year")
                            .setPositiveButton("OK", null)
                            .show();
                    });
                    
                    // Add close button to dialog footer
                    Button closeButton = new Button(requireContext());
                    closeButton.setText("Close");
                    closeButton.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    closeButton.setOnClickListener(v2 -> dialog.dismiss());
                    
                    // Create a layout for the close button
                    LinearLayout buttonLayout = new LinearLayout(requireContext());
                    buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
                    buttonLayout.setGravity(android.view.Gravity.END);
                    buttonLayout.setPadding(20, 20, 20, 20);
                    buttonLayout.addView(closeButton);
                    
                    // Add layout to parent after CardView
                    ViewGroup csvParent = (ViewGroup) csvView;
                    csvParent.addView(buttonLayout);
                    
                    dialog.show();
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error showing student import dialog: " + e.getMessage(), e);
                    Toast.makeText(requireContext(), 
                        "Error showing import dialog: " + e.getMessage(), 
                        Toast.LENGTH_LONG).show();
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated", e);
            Toast.makeText(requireContext(), "Error initializing placement dashboard", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize the ActivityResultLauncher for CSV file selection
        csvPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            this::handleCsvFileSelected
        );
    }
    
    // Handler for CSV file selection result
    private void handleCsvFileSelected(Uri uri) {
        if (uri == null) {
            Toast.makeText(requireContext(), "File selection cancelled", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            selectedCsvUri = uri;
            String fileName = getFileNameFromUri(uri);
            
            // Update UI with selected file name - we need to find the currently showing dialog
            AlertDialog currentDialog = null;
            
            // Find any active dialogs
            for (Fragment fragment : getParentFragmentManager().getFragments()) {
                if (fragment.getView() != null) {
                    ViewGroup root = (ViewGroup) fragment.getView().getRootView();
                    TextView txtSelectedFile = root.findViewById(R.id.txt_selected_file);
                    if (txtSelectedFile != null) {
                        txtSelectedFile.setText(fileName != null ? fileName : "Selected file");
                        
                        // Enable upload button
                        Button btnUploadCsv = root.findViewById(R.id.btn_upload_csv);
                        if (btnUploadCsv != null) {
                            btnUploadCsv.setEnabled(true);
                        }
                        break;
                    }
                }
            }
            
            // If we couldn't find it through fragments, try the current view hierarchy
            if (getView() != null) {
                View rootView = getView().getRootView();
                TextView txtSelectedFile = rootView.findViewById(R.id.txt_selected_file);
                if (txtSelectedFile != null) {
                    txtSelectedFile.setText(fileName != null ? fileName : "Selected file");
                    
                    // Enable upload button
                    Button btnUploadCsv = rootView.findViewById(R.id.btn_upload_csv);
                    if (btnUploadCsv != null) {
                        btnUploadCsv.setEnabled(true);
                    }
                }
            }
            
            Toast.makeText(requireContext(), "File selected: " + fileName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error handling selected file", e);
            Toast.makeText(requireContext(), "Error selecting file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    // Helper method to get filename from URI
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        
        try {
            if (uri.getScheme().equals("content")) {
                try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (nameIndex >= 0) {
                            result = cursor.getString(nameIndex);
                        }
                    }
                }
            }
            
            if (result == null) {
                result = uri.getLastPathSegment();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting filename from URI", e);
        }
        
        return result != null ? result : "Selected File";
    }
    
    private void loadStudents() {
        LiveData<List<Student>> studentsLiveData = db.studentDao().getAllStudents();
        
        studentsLiveData.observe(getViewLifecycleOwner(), students -> {
            if (students != null && !students.isEmpty()) {
                allStudents = students;
                filteredStudents.clear();
                filteredStudents.addAll(students);
                updateUI(filteredStudents);
            } else {
                // If no data in database, load sample data
                loadSampleData();
            }
        });
    }
    
    private void loadSampleData() {
        allStudents.clear();
        
        // Add sample students with diverse attributes for testing filters
        allStudents.add(new Student("60004200001", "Raj Patel", "raj.patel@example.com", "password123", 
                "BTech Computer Engineering", "CS2001", 3.8f, "Boy", 0, "2024"));
        
        allStudents.add(new Student("60004200002", "Priya Sharma", "priya.sharma@example.com", "password123", 
                "BTech Information Technology", "IT2001", 3.9f, "Girl", 0, "2024"));
        
        allStudents.add(new Student("60004200003", "Amit Kumar", "amit.kumar@example.com", "password123", 
                "BTech CSE (Cyber Security)", "CS2002", 3.5f, "Boy", 1, "2024"));
        
        allStudents.add(new Student("60004200004", "Neha Singh", "neha.singh@example.com", "password123", 
                "BTech Data Science", "DS2001", 3.7f, "Girl", 0, "2024"));
        
        allStudents.add(new Student("60004200005", "Rohit Verma", "rohit.verma@example.com", "password123", 
                "BTech Artificial Intelligence", "AI2001", 3.2f, "Boy", 2, "2024"));
        
        allStudents.add(new Student("60004200006", "Anjali Desai", "anjali.desai@example.com", "password123", 
                "BTech Civil Engineering", "CE2001", 3.4f, "Girl", 0, "2024"));
        
        allStudents.add(new Student("60004200007", "Karan Malhotra", "karan.malhotra@example.com", "password123", 
                "BTech Mechatronics Engineering", "ME2001", 2.9f, "Boy", 1, "2024"));
        
        allStudents.add(new Student("60004200008", "Shikha Joshi", "shikha.joshi@example.com", "password123", 
                "BTech Electrical and Telecommunication Engineering", "ETE2001", 3.1f, "Girl", 0, "2024"));
        
        allStudents.add(new Student("60004200009", "Vikram Singh", "vikram.singh@example.com", "password123", 
                "BTech Computer Science and Business Systems", "CSBS2001", 3.6f, "Boy", 0, "2024"));
        
        allStudents.add(new Student("60004200010", "Pooja Gupta", "pooja.gupta@example.com", "password123", 
                "BTech Mechanical", "ME2002", 3.3f, "Girl", 1, "2024"));
                
        filteredStudents.clear();
        filteredStudents.addAll(allStudents);
        updateUI(filteredStudents);
    }
    
    private void updateUI(List<Student> students) {
        if (students != null && !students.isEmpty()) {
            studentAdapter.setStudents(students);
            studentsTable.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        } else {
            studentsTable.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }
    
    private void filterStudents(String query) {
        if (query.isEmpty()) {
            updateUI(filteredStudents);
            return;
        }
        
        List<Student> searchResults = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase(Locale.getDefault());
        
        for (Student student : filteredStudents) {
            if (student.getFullName().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery) ||
                student.getBranch().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery) ||
                student.getSapId().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)) {
                searchResults.add(student);
            }
        }
        
        updateUI(searchResults);
    }
    
    private void applyFilters() {
        try {
            filteredStudents = new ArrayList<>(allStudents);

            // Apply branch filter
            if (branchFilter != null && branchFilter.getSelectedItemPosition() >= 0 && 
                branchFilter.getSelectedItemPosition() < branchOptions.length) {
                String branchValue = branchOptions[branchFilter.getSelectedItemPosition()];
                if (!branchValue.equals("All Branches")) {
                    List<Student> branchFiltered = new ArrayList<>();
                    for (Student student : filteredStudents) {
                        if (student.getBranch().equals(branchValue)) {
                            branchFiltered.add(student);
                        }
                    }
                    filteredStudents = branchFiltered;
                }
            }

            // Apply roll number range filter
            String rollMinStr = rollMinFilter != null ? rollMinFilter.getText().toString() : "";
            String rollMaxStr = rollMaxFilter != null ? rollMaxFilter.getText().toString() : "";
            if (!rollMinStr.isEmpty() || !rollMaxStr.isEmpty()) {
                try {
                    int rollMin = rollMinStr.isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(rollMinStr);
                    int rollMax = rollMaxStr.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(rollMaxStr);
                    
                    List<Student> rollFiltered = new ArrayList<>();
                    for (Student student : filteredStudents) {
                        try {
                            String rollNumberStr = student.getRollNumber().replaceAll("[^0-9]", "");
                            if (!rollNumberStr.isEmpty()) {
                                int rollNumber = Integer.parseInt(rollNumberStr);
                                if (rollNumber >= rollMin && rollNumber <= rollMax) {
                                    rollFiltered.add(student);
                                }
                            }
                        } catch (NumberFormatException e) {
                            // Skip this student if roll number cannot be parsed
                            Log.e(TAG, "Error parsing roll number: " + student.getRollNumber(), e);
                        }
                    }
                    filteredStudents = rollFiltered;
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Invalid roll number format", Toast.LENGTH_SHORT).show();
                }
            }

            // Apply CGPA filter
            if (cgpaFilter != null && cgpaFilter.getSelectedItemPosition() >= 0 && 
                cgpaFilter.getSelectedItemPosition() < cgpaOptions.length) {
                String cgpaValue = cgpaOptions[cgpaFilter.getSelectedItemPosition()];
                if (!cgpaValue.equals("Any CGPA")) {
                    try {
                        // Extract numeric part by removing the '+' character
                        float minCgpa = Float.parseFloat(cgpaValue.replace("+", ""));
                        List<Student> cgpaFiltered = new ArrayList<>();
                        for (Student student : filteredStudents) {
                            if (student.getCgpa() >= minCgpa) {
                                cgpaFiltered.add(student);
                            }
                        }
                        filteredStudents = cgpaFiltered;
                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(), "Invalid CGPA format", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            // Apply gender filter
            if (genderFilter != null && genderFilter.getSelectedItemPosition() >= 0 && 
                genderFilter.getSelectedItemPosition() < genderOptions.length) {
                String genderValue = genderOptions[genderFilter.getSelectedItemPosition()];
                if (!genderValue.equals("All")) {
                    List<Student> genderFiltered = new ArrayList<>();
                    for (Student student : filteredStudents) {
                        if (student.getGender().equals(genderValue)) {
                            genderFiltered.add(student);
                        }
                    }
                    filteredStudents = genderFiltered;
                }
            }

            // Apply backlogs filter
            String backlogsStr = backlogsFilter != null ? backlogsFilter.getText().toString() : "";
            if (!backlogsStr.isEmpty()) {
                try {
                    int maxBacklogs = Integer.parseInt(backlogsStr);
                    List<Student> backlogsFiltered = new ArrayList<>();
                    for (Student student : filteredStudents) {
                        if (student.getBacklogs() <= maxBacklogs) {
                            backlogsFiltered.add(student);
                        }
                    }
                    filteredStudents = backlogsFiltered;
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Invalid backlogs format", Toast.LENGTH_SHORT).show();
                }
            }

            // Update UI with filtered students
            updateUI(filteredStudents);
            
            // Notify user of filter results
            Toast.makeText(requireContext(), 
                    "Showing " + filteredStudents.size() + " of " + allStudents.size() + " students", 
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error applying filters: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error applying filters", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showCompanyInfoDialog() {
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_company_information);
        
        // Setup date pickers and dropdowns
        setupCompanyInfoDialogFields(dialog);
        
        // Setup send button
        Button sendButton = dialog.findViewById(R.id.send_to_students_button);
        sendButton.setOnClickListener(v -> {
            // Get selected students
            List<Student> selectedStudents = studentAdapter.getSelectedStudents();
            
            if (selectedStudents.isEmpty()) {
                Toast.makeText(requireContext(), 
                        "Please select at least one student", 
                        Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Get company info fields
            EditText companyNameField = dialog.findViewById(R.id.company_name);
            EditText jobTitleField = dialog.findViewById(R.id.job_title);
            EditText jobLocationField = dialog.findViewById(R.id.job_location);
            EditText packageField = dialog.findViewById(R.id.ctc);
            EditText jobDescriptionField = dialog.findViewById(R.id.job_description);
            
            AutoCompleteTextView industryTypeField = dialog.findViewById(R.id.industry_type);
            AutoCompleteTextView workTypeField = dialog.findViewById(R.id.work_type);
            AutoCompleteTextView jobTypeField = dialog.findViewById(R.id.job_type);
            AutoCompleteTextView interviewModeField = dialog.findViewById(R.id.interview_mode);
            AutoCompleteTextView driveModeField = dialog.findViewById(R.id.drive_mode);
            
            TextInputEditText startDateField = dialog.findViewById(R.id.start_date);
            TextInputEditText deadlineField = dialog.findViewById(R.id.deadline);
            TextInputEditText driveDateField = dialog.findViewById(R.id.drive_date);
            
            // Validate required fields
            String companyName = companyNameField.getText().toString().trim();
            String jobTitle = jobTitleField.getText().toString().trim();
            String jobLocation = jobLocationField.getText().toString().trim();
            String driveDate = driveDateField.getText().toString().trim();
            
            if (companyName.isEmpty() || jobTitle.isEmpty() || driveDate.isEmpty()) {
                Toast.makeText(requireContext(), 
                        "Please fill all required fields", 
                        Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Create a unique drive ID
            String driveId = generateDriveId(companyName);
            
            // Create the DriveInfo object
            DriveInfo driveInfo = new DriveInfo();
            driveInfo.setId(driveId);
            driveInfo.setCompanyName(companyName);
            driveInfo.setJobTitle(jobTitle);
            driveInfo.setLocation(jobLocation);
            driveInfo.setPackageOffered(packageField.getText().toString().trim());
            driveInfo.setJobDescription(jobDescriptionField.getText().toString().trim());
            
            driveInfo.setIndustryType(industryTypeField.getText().toString().trim());
            driveInfo.setWorkType(workTypeField.getText().toString().trim());
            driveInfo.setJobType(jobTypeField.getText().toString().trim());
            driveInfo.setInterviewMode(interviewModeField.getText().toString().trim());
            driveInfo.setDriveMode(driveModeField.getText().toString().trim());
            
            driveInfo.setStartDate(startDateField.getText().toString().trim());
            driveInfo.setDeadline(deadlineField.getText().toString().trim());
            driveInfo.setDriveDate(driveDate);
            
            // Determine status based on current date vs drive date
            String status = determineDriveStatus(driveDate);
            driveInfo.setStatus(status);
            
            // Save the drive info to database for each selected student
            Toast.makeText(requireContext(), 
                    "Saving drive information for " + selectedStudents.size() + " students...", 
                    Toast.LENGTH_SHORT).show();
            
            // Actually save to database using the executor
            executor.execute(() -> {
                try {
                    // Insert the drive
                    long result = db.driveDao().insert(driveInfo);
                    
                    Log.d(TAG, "Drive inserted with result: " + result + ", ID: " + driveId);
                    
                    // Create and insert StudentDrive relationships
                    for (Student student : selectedStudents) {
                        StudentDrive studentDrive = new StudentDrive();
                        studentDrive.setDriveId(driveId);
                        studentDrive.setStudentSapId(student.getSapId());
                        studentDrive.setStatus("notified");
                        studentDrive.setApplied(false);
                        
                        long sdResult = db.studentDriveDao().insert(studentDrive);
                        Log.d(TAG, "StudentDrive inserted: " + sdResult + " for student: " + student.getSapId());
                    }
                    
                    // Update UI on main thread
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), 
                                    "Company information sent to " + selectedStudents.size() + " students", 
                                    Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            
                            // Show notification demo
                            showDemoDriveAssignedNotification(companyName, jobTitle);
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error saving drive: " + e.getMessage(), e);
                    
                    // Show error on main thread
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), 
                                    "Error: " + e.getMessage(), 
                                    Toast.LENGTH_LONG).show();
                        });
                    }
                }
            });
        });
        
        dialog.setOnDismissListener(dialogInterface -> 
            Log.d(TAG, "Company info dialog dismissed")
        );
        
        dialog.show();
    }
    
    private String generateDriveId(String companyName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String prefix = companyName.replaceAll("[^A-Za-z0-9]", "");
        prefix = prefix.substring(0, Math.min(3, prefix.length()));
        return prefix.toUpperCase() + timestamp.substring(timestamp.length() - 6);
    }
    
    private void showDemoDriveAssignedNotification(String companyName, String jobTitle) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Notification Sent")
            .setMessage("Students will receive the following notifications:\n\n" +
                    "1. Immediate: \"You've been selected for " + jobTitle + " at " + companyName + "\"\n\n" +
                    "2. 1 day before: \"Reminder: Drive at " + companyName + " tomorrow\"\n\n" +
                    "3. 1 hour before: \"Reminder: Drive at " + companyName + " in 1 hour\"")
            .setPositiveButton("OK", null)
            .show();
    }
    
    // StudentTableAdapter.OnStudentItemClickListener implementation
    @Override
    public void onStudentItemClick(Student student, int position) {
        // Show student details dialog or navigate to details screen
        Toast.makeText(requireContext(), "Selected: " + student.getFullName(), Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onStudentSelectionChanged(List<Student> selectedStudents) {
        // Update UI with selection count
        int count = selectedStudents.size();
        if (count > 0) {
            Toast.makeText(requireContext(), count + " students selected", Toast.LENGTH_SHORT).show();
            
            // Update select all checkbox without triggering listener
            selectAllCheckbox.setOnCheckedChangeListener(null);
            selectAllCheckbox.setChecked(count == filteredStudents.size());
            selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    studentAdapter.selectAll();
                } else {
                    studentAdapter.clearSelection();
                }
            });
        }
    }
    
    /**
     * Set up the dropdown adapters for filters
     */
    private void setupDropdowns() {
        try {
            // Branch dropdown
            if (branchFilter != null) {
                ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(
                        requireContext(), android.R.layout.simple_spinner_item, branchOptions);
                branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                branchFilter.setAdapter(branchAdapter);
            } else {
                Log.e(TAG, "branchFilter is null");
            }

            // CGPA dropdown
            if (cgpaFilter != null) {
                ArrayAdapter<String> cgpaAdapter = new ArrayAdapter<>(
                        requireContext(), android.R.layout.simple_spinner_item, cgpaOptions);
                cgpaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                cgpaFilter.setAdapter(cgpaAdapter);
            } else {
                Log.e(TAG, "cgpaFilter is null");
            }

            // Gender dropdown
            if (genderFilter != null) {
                ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                        requireContext(), android.R.layout.simple_spinner_item, genderOptions);
                genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                genderFilter.setAdapter(genderAdapter);
            } else {
                Log.e(TAG, "genderFilter is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up dropdowns: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error setting up filters", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Shows a detailed report of CSV processing errors
     */
    private void showCsvErrorReportDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("CSV Processing Issues");
            
            // Create a scrollable view for the error report
            ScrollView scrollView = new ScrollView(requireContext());
            scrollView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            
            // Create error report content
            LinearLayout contentLayout = new LinearLayout(requireContext());
            contentLayout.setOrientation(LinearLayout.VERTICAL);
            contentLayout.setPadding(30, 20, 30, 20);
            
            // Add a title text
            TextView titleText = new TextView(requireContext());
            titleText.setText("The following issues occurred during CSV processing:");
            titleText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
            titleText.setTextSize(16);
            titleText.setPadding(0, 0, 0, 20);
            contentLayout.addView(titleText);
            
            // Add some sample errors - in a real app, these would come from actual CSV processing
            String[] errors = {
                "Row 3: Invalid email format 'john.doe@'",
                "Row 5: SAP ID '60004200001' is a duplicate of row 1",
                "Row 7: Missing required field 'branch'",
                "Row 8: CGPA value '11.2' is out of valid range (0-10)",
                "Row 10: Invalid backlogs value '-2' (must be non-negative)"
            };
            
            // Add each error as a text view
            for (String error : errors) {
                TextView errorText = new TextView(requireContext());
                errorText.setText("• " + error);
                errorText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
                errorText.setPadding(10, 8, 10, 8);
                contentLayout.addView(errorText);
            }
            
            // Add a note about correcting errors
            TextView noteText = new TextView(requireContext());
            noteText.setText("\nPlease correct these issues in your CSV file and try uploading again. " +
                    "You can download a sample CSV template for reference.");
            noteText.setPadding(0, 20, 0, 10);
            contentLayout.addView(noteText);
            
            // Add the content layout to the scroll view
            scrollView.addView(contentLayout);
            
            // Set the scroll view as the dialog content
            builder.setView(scrollView);
            
            // Add buttons
            builder.setPositiveButton("OK", null);
            builder.setNeutralButton("Download Sample", (dialog, which) -> {
                // Trigger the download sample functionality
                Button btnDownloadSample = getView().findViewById(R.id.btn_download_sample);
                if (btnDownloadSample != null) {
                    btnDownloadSample.performClick();
                } else {
                    Toast.makeText(requireContext(), 
                        "Download sample template functionality not available", 
                        Toast.LENGTH_SHORT).show();
                }
            });
            
            builder.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing CSV error report: " + e.getMessage(), e);
            Toast.makeText(requireContext(), 
                "Error showing CSV processing report", 
                Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Set up date pickers and dropdowns in the company info dialog
     */
    private void setupCompanyInfoDialogFields(Dialog dialog) {
        try {
            // Initialize TextInputEditText fields for dates
            TextInputEditText startDateField = dialog.findViewById(R.id.start_date);
            TextInputEditText deadlineField = dialog.findViewById(R.id.deadline);
            TextInputEditText driveDateField = dialog.findViewById(R.id.drive_date);
            
            // Set up date picker dialogs for date fields
            setDatePicker(startDateField);
            setDatePicker(deadlineField);
            setDatePicker(driveDateField);
            
            // Set up industry type dropdown
            AutoCompleteTextView industryTypeField = dialog.findViewById(R.id.industry_type);
            String[] industryTypes = {"Information Technology", "Finance", "Healthcare", "Education", 
                    "Manufacturing", "Retail", "Consulting", "Other"};
            setupDropdown(industryTypeField, industryTypes);
            
            // Set up work type dropdown
            AutoCompleteTextView workTypeField = dialog.findViewById(R.id.work_type);
            String[] workTypes = {"Full-time", "Part-time", "Internship", "Contract"};
            setupDropdown(workTypeField, workTypes);
            
            // Set up job type dropdown
            AutoCompleteTextView jobTypeField = dialog.findViewById(R.id.job_type);
            String[] jobTypes = {"Software Engineer", "Data Analyst", "Product Manager", 
                    "Business Analyst", "Marketing", "Sales", "Operations", "Other"};
            setupDropdown(jobTypeField, jobTypes);
            
            // Set up interview mode dropdown
            AutoCompleteTextView interviewModeField = dialog.findViewById(R.id.interview_mode);
            String[] interviewModes = {"Online", "In-person", "Hybrid"};
            setupDropdown(interviewModeField, interviewModes);
            
            // Set up drive mode dropdown
            AutoCompleteTextView driveModeField = dialog.findViewById(R.id.drive_mode);
            String[] driveModes = {"On-campus", "Off-campus", "Virtual"};
            setupDropdown(driveModeField, driveModes);
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up company info dialog fields: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error setting up form fields", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Set up date picker dialog for a TextInputEditText field
     */
    private void setDatePicker(TextInputEditText dateField) {
        dateField.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            
            android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(
                    requireContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format(Locale.US, "%02d/%02d/%04d", 
                                selectedMonth + 1, selectedDay, selectedYear);
                        dateField.setText(formattedDate);
                    }, year, month, day);
            
            datePickerDialog.show();
        });
    }
    
    /**
     * Set up dropdown for AutoCompleteTextView
     */
    private void setupDropdown(AutoCompleteTextView textView, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, items);
        textView.setAdapter(adapter);
    }
    
    /**
     * Determine the status of a drive based on its date
     */
    private String determineDriveStatus(String driveDateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            Date driveDate = dateFormat.parse(driveDateStr);
            Date currentDate = new Date();
            
            if (driveDate == null) {
                return "upcoming"; // Default to upcoming if date parsing fails
            }
            
            Calendar driveCal = Calendar.getInstance();
            driveCal.setTime(driveDate);
            driveCal.set(Calendar.HOUR_OF_DAY, 0);
            driveCal.set(Calendar.MINUTE, 0);
            driveCal.set(Calendar.SECOND, 0);
            
            Calendar currentCal = Calendar.getInstance();
            currentCal.setTime(currentDate);
            currentCal.set(Calendar.HOUR_OF_DAY, 0);
            currentCal.set(Calendar.MINUTE, 0);
            currentCal.set(Calendar.SECOND, 0);
            
            if (driveCal.after(currentCal)) {
                return "upcoming";
            } else if (driveCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR) &&
                    driveCal.get(Calendar.DAY_OF_YEAR) == currentCal.get(Calendar.DAY_OF_YEAR)) {
                return "ongoing";
            } else {
                return "done";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error determining drive status: " + e.getMessage(), e);
            return "upcoming"; // Default to upcoming
        }
    }
    
    /**
     * Parse CSV data from an input stream and convert it into Student objects
     * 
     * @param inputStream The input stream containing CSV data
     * @return A list of Student objects parsed from the CSV
     * @throws IOException If there's an error reading the CSV data
     */
    private List<Student> parseCSV(InputStream inputStream) throws IOException {
        List<Student> students = new ArrayList<>();
        
        if (inputStream == null) {
            throw new IOException("Invalid CSV file");
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // Skip header row
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                // Split the CSV line into parts
                String[] parts = line.split(",");
                
                // Basic validation
                if (parts.length < 6) {
                    Log.w(TAG, "Skipping invalid row: " + line);
                    continue;
                }
                
                try {
                    // Create a new Student object from CSV data
                    Student student = new Student();
                    student.setSapId(parts[0].trim());
                    student.setFullName(parts[1].trim());
                    student.setEmail(parts[2].trim());
                    student.setPassword(parts[3].trim());
                    student.setBranch(parts[4].trim());
                    student.setRollNumber(parts[5].trim());
                    
                    // Optional fields
                    if (parts.length > 6 && !parts[6].trim().isEmpty()) {
                        try {
                            student.setCgpa(Float.parseFloat(parts[6].trim()));
                        } catch (NumberFormatException e) {
                            Log.w(TAG, "Invalid CGPA format: " + parts[6].trim());
                        }
                    }
                    
                    if (parts.length > 7 && !parts[7].trim().isEmpty()) {
                        student.setGender(parts[7].trim());
                    }
                    
                    if (parts.length > 8 && !parts[8].trim().isEmpty()) {
                        try {
                            student.setBacklogs(Integer.parseInt(parts[8].trim()));
                        } catch (NumberFormatException e) {
                            Log.w(TAG, "Invalid backlogs format: " + parts[8].trim());
                        }
                    }
                    
                    if (parts.length > 9 && !parts[9].trim().isEmpty()) {
                        student.setBatch(parts[9].trim());
                    }
                    
                    students.add(student);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing student row: " + line, e);
                }
            }
        }
        
        // Save students to database in batch
        if (!students.isEmpty()) {
            executor.execute(() -> {
                try {
                    // Insert students into database
                    for (Student student : students) {
                        db.studentDao().insert(student);
                    }
                    Log.d(TAG, "Saved " + students.size() + " students to database");
                } catch (Exception e) {
                    Log.e(TAG, "Error saving students to database", e);
                }
            });
        }
        
        return students;
    }
} 