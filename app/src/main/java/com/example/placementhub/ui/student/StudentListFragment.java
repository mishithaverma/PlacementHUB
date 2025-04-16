package com.example.placementhub.ui.student;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placementhub.R;
import com.example.placementhub.data.models.Student;
import com.example.placementhub.utils.CsvHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class StudentListFragment extends Fragment {
    private static final String TAG = "StudentListFragment";
    private static final int PERMISSION_REQUEST_CODE = 123;

    private StudentViewModel viewModel;
    private RecyclerView recyclerView;
    private Button btnSelectCsv;
    private Button btnUploadCsv;
    private Button btnDownloadSample;
    private TextView txtSelectedFile;
    private CheckBox chkOverwriteDuplicates;
    private ProgressBar progressUpload;
    private TextView txtUploadStatus;
    private ImageButton btnCsvInfo;
    
    private Uri selectedCsvUri;
    private final Executor executor = Executors.newSingleThreadExecutor();
    
    // Activity Result Launcher for CSV file picker
    private final ActivityResultLauncher<String> csvPickerLauncher = 
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    this::handleCsvFileSelected);

    // Activity Result Launcher for permissions
    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                boolean allGranted = true;
                for (Boolean isGranted : permissions.values()) {
                    allGranted = allGranted && isGranted;
                }
                
                if (allGranted) {
                    openCsvFilePicker();
                } else {
                    // Show a toast instead of calling the missing method
                    Toast.makeText(requireContext(), 
                        "Storage permission is required to select CSV files", 
                        Toast.LENGTH_LONG).show();
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        // Create a simple layout as fallback in case of errors
        LinearLayout fallbackLayout = new LinearLayout(requireContext());
        fallbackLayout.setOrientation(LinearLayout.VERTICAL);
        fallbackLayout.setGravity(android.view.Gravity.CENTER);
        fallbackLayout.setPadding(50, 50, 50, 50);
        
        // Early logging
        Log.d(TAG, "StudentListFragment.onCreateView starting");
        
        try {
            // Inflate the view with a try-catch to prevent crashes
            View root = inflater.inflate(R.layout.fragment_student_list, container, false);
            Log.d(TAG, "View inflation successful");
            
            // Initialize basic UI first (most important part)
            try {
                btnSelectCsv = root.findViewById(R.id.btn_select_csv);
                btnUploadCsv = root.findViewById(R.id.btn_upload_csv);
                btnDownloadSample = root.findViewById(R.id.btn_download_sample);
                txtSelectedFile = root.findViewById(R.id.txt_selected_file);
                chkOverwriteDuplicates = root.findViewById(R.id.chk_overwrite_duplicates);
                progressUpload = root.findViewById(R.id.progress_upload);
                txtUploadStatus = root.findViewById(R.id.txt_upload_status);
                btnCsvInfo = root.findViewById(R.id.btn_csv_info);
                Log.d(TAG, "Basic UI elements initialized");
                
                // Set basic click listeners
                btnSelectCsv.setOnClickListener(v -> {
                    try {
                        checkPermissionAndOpenFilePicker();
                    } catch (Exception e) {
                        Log.e(TAG, "Error in select CSV listener", e);
                        Toast.makeText(requireContext(), "Error selecting file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                
                btnDownloadSample.setOnClickListener(v -> {
                    try {
                        downloadSampleCsv();
                    } catch (Exception e) {
                        Log.e(TAG, "Error in download sample listener", e);
                        Toast.makeText(requireContext(), "Error downloading sample: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                
                btnUploadCsv.setOnClickListener(v -> {
                    try {
                        uploadCsvFile();
                    } catch (Exception e) {
                        Log.e(TAG, "Error in upload CSV listener", e);
                        Toast.makeText(requireContext(), "Error uploading file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                
                btnCsvInfo.setOnClickListener(v -> {
                    try {
                        showCsvInfoDialog();
                    } catch (Exception e) {
                        Log.e(TAG, "Error showing CSV info", e);
                        Toast.makeText(requireContext(), "Error showing CSV info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d(TAG, "Click listeners set");
            } catch (Exception e) {
                Log.e(TAG, "Error initializing basic UI", e);
                Toast.makeText(requireContext(), "Error initializing UI: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            
            // Set up RecyclerView and ViewModel (less critical)
            try {
                // RecyclerView setup
                recyclerView = root.findViewById(R.id.recycler_students);
                if (recyclerView != null) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = new View(parent.getContext());
                            return new RecyclerView.ViewHolder(view) {};
                        }
        
                        @Override
                        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                            // Nothing to bind
                        }
        
                        @Override
                        public int getItemCount() {
                            return 0;
                        }
                    });
                }
                Log.d(TAG, "RecyclerView setup complete");
                
                // Initialize ViewModel in a separate try-catch
                try {
                    viewModel = new ViewModelProvider(this).get(StudentViewModel.class);
                    Log.d(TAG, "ViewModel initialized successfully");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to initialize ViewModel", e);
                    // Continue without ViewModel
                }
            } catch (Exception e) {
                Log.e(TAG, "Error setting up secondary UI components", e);
                // Not critical, continue
            }
            
            Log.d(TAG, "onCreateView completed successfully");
            return root;
            
        } catch (Exception e) {
            Log.e(TAG, "Critical error in fragment creation", e);
            
            // Create error message
            TextView errorView = new TextView(requireContext());
            errorView.setText("Error loading student import screen: " + e.getMessage());
            errorView.setPadding(20, 20, 20, 20);
            
            // Create retry button
            Button retryButton = new Button(requireContext());
            retryButton.setText("Retry");
            retryButton.setOnClickListener(v -> {
                try {
                    // Recreate fragment
                    getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, new StudentListFragment())
                        .commit();
                } catch (Exception ex) {
                    Log.e(TAG, "Error recreating fragment", ex);
                    Toast.makeText(requireContext(), "Could not retry: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            
            // Create go back button
            Button backButton = new Button(requireContext());
            backButton.setText("Go Back");
            backButton.setOnClickListener(v -> {
                try {
                    // Go back to previous fragment
                    getParentFragmentManager().popBackStack();
                } catch (Exception ex) {
                    Log.e(TAG, "Error going back", ex);
                    // Try an alternative approach if popBackStack fails
                    try {
                        getActivity().onBackPressed();
                    } catch (Exception ex2) {
                        Log.e(TAG, "Error with alternative back navigation", ex2);
                    }
                }
            });
            
            // Add views to the fallback layout
            fallbackLayout.addView(errorView);
            fallbackLayout.addView(retryButton);
            fallbackLayout.addView(backButton);
            
            return fallbackLayout;
        }
    }

    private void checkPermissionAndOpenFilePicker() {
        try {
            // On newer Android versions, we'll use the system's file picker
            // which doesn't require explicit storage permissions
            openCsvFilePicker();
        } catch (Exception e) {
            Log.e(TAG, "Error checking permissions", e);
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openCsvFilePicker() {
        try {
            Toast.makeText(requireContext(), "Select a CSV file", Toast.LENGTH_SHORT).show();
            
            // Use multiple MIME types to improve file selection on different devices
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // For newer Android versions
                String[] mimeTypes = {"text/csv", "text/comma-separated-values", "text/plain", "*/*"};
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.setType(mimeTypes[0]);
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                } else {
                    intent.setType("*/*");
                }
                
                try {
                    startActivityForResult(Intent.createChooser(intent, "Select CSV File"), PERMISSION_REQUEST_CODE);
                    return;
                } catch (android.content.ActivityNotFoundException ex) {
                    // Fallback to simple method if chooser fails
                    Log.e(TAG, "No file explorer app found", ex);
                }
            }
            
            // Fallback to simpler method
            csvPickerLauncher.launch("text/*");
        } catch (Exception e) {
            Log.e(TAG, "Error opening file picker", e);
            Toast.makeText(requireContext(), "Error opening file picker: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            
            // One more fallback attempt with the most generic type
            try {
                csvPickerLauncher.launch("*/*");
            } catch (Exception e2) {
                Log.e(TAG, "Final fallback also failed", e2);
            }
        }
    }
    
    private void handleCsvFileSelected(Uri uri) {
        try {
            if (uri == null) {
                Log.e(TAG, "Selected URI is null");
                Toast.makeText(requireContext(), "File selection failed or was canceled", Toast.LENGTH_SHORT).show();
                txtSelectedFile.setText("No file selected");
                btnUploadCsv.setEnabled(false);
                return;
            }

            selectedCsvUri = uri;
            String fileName = getFileNameFromUri(uri);
            Log.d(TAG, "File selected: " + uri + ", name: " + fileName);
            
            // Update UI
            txtSelectedFile.setText(fileName != null ? fileName : "Selected file");
            
            // Validate the file
            ContentResolver resolver = requireContext().getContentResolver();
            String mimeType = resolver.getType(uri);
            Log.d(TAG, "File mime type: " + mimeType);
            
            // Use a simple background thread to check if we can read the file
            new Thread(() -> {
                boolean canRead = false;
                String errorMessage = null;
                
                try (InputStream is = resolver.openInputStream(uri)) {
                    if (is == null) {
                        errorMessage = "Cannot open file stream";
                    } else {
                        // Read a small amount to verify access
                        byte[] buffer = new byte[1024];
                        int bytesRead = is.read(buffer);
                        canRead = bytesRead > 0;
                        
                        // Check if it looks like a CSV (contains commas)
                        boolean containsCommas = false;
                        for (int i = 0; i < bytesRead; i++) {
                            if (buffer[i] == ',') {
                                containsCommas = true;
                                break;
                            }
                        }
                        
                        if (!containsCommas) {
                            errorMessage = "File doesn't appear to be a CSV (no commas found)";
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error reading file", e);
                    errorMessage = "Error reading file: " + e.getMessage();
                }
                
                final boolean finalCanRead = canRead;
                final String finalErrorMessage = errorMessage;
                
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        if (finalCanRead && finalErrorMessage == null) {
                            btnUploadCsv.setEnabled(true);
                            Toast.makeText(requireContext(), "CSV file ready to upload", Toast.LENGTH_SHORT).show();
                        } else {
                            btnUploadCsv.setEnabled(false);
                            txtSelectedFile.setText("Invalid file: " + fileName);
                            Toast.makeText(requireContext(), 
                                finalErrorMessage != null ? finalErrorMessage : "Error validating file", 
                                Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            Log.e(TAG, "Error handling selected file", e);
            Toast.makeText(requireContext(), "Error handling selected file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            btnUploadCsv.setEnabled(false);
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        try {
            if ("content".equals(uri.getScheme())) {
                try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri)) {
                    if (inputStream != null) {
                        // Try to get the display name from the content provider
                        String[] projection = {android.provider.MediaStore.MediaColumns.DISPLAY_NAME};
                        android.database.Cursor cursor = requireContext().getContentResolver().query(
                                uri, projection, null, null, null);
                        if (cursor != null) {
                            if (cursor.moveToFirst()) {
                                int columnIndex = cursor.getColumnIndexOrThrow(
                                        android.provider.MediaStore.MediaColumns.DISPLAY_NAME);
                                result = cursor.getString(columnIndex);
                                Log.d(TAG, "File name from cursor: " + result);
                            }
                            cursor.close();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error getting file name from content URI", e);
                }
            }
            
            if (result == null) {
                // Fallback to URI path
                result = uri.getLastPathSegment();
                Log.d(TAG, "File name from last path segment: " + result);
            }
            
            if (result == null) {
                // Final fallback
                result = uri.toString();
                // Extract file name from path if possible
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
                Log.d(TAG, "File name from URI string: " + result);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting file name from URI", e);
            result = "Selected File";
        }
        
        return result != null ? result : "Selected File";
    }

    private void uploadCsvFile() {
        try {
            if (selectedCsvUri == null) {
                Toast.makeText(getContext(), "Please select a CSV file first", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show progress
            progressUpload.setVisibility(View.VISIBLE);
            progressUpload.setIndeterminate(true);
            txtUploadStatus.setVisibility(View.VISIBLE);
            txtUploadStatus.setText("Processing file...");
            btnUploadCsv.setEnabled(false);
            btnSelectCsv.setEnabled(false);

            // Use a background thread with extra error handling
            new Thread(() -> {
                try {
                    // Parse the CSV file
                    CsvHelper.ParseResult result = CsvHelper.parseStudentCsv(requireContext(), selectedCsvUri);
                    
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            try {
                                if (result == null) {
                                    throw new Exception("Parse result is null");
                                }
                                
                                // Handle errors
                                if (result.hasErrors()) {
                                    showErrorDialog(result.getErrors());
                                    txtUploadStatus.setText("Upload failed with errors");
                                    progressUpload.setIndeterminate(false);
                                    progressUpload.setProgress(0);
                                } else if (result.hasStudents()) {
                                    // Success - process students without database
                                    int studentCount = result.getStudents().size();
                                    txtUploadStatus.setText("Processed " + studentCount + " students successfully");
                                    progressUpload.setIndeterminate(false);
                                    progressUpload.setProgress(100);
                                    
                                    // Show success message
                                    Toast.makeText(requireContext(), 
                                        "Successfully processed " + studentCount + " students", 
                                        Toast.LENGTH_SHORT).show();
                                    
                                    // Only attempt database insertion if ViewModel is available
                                    if (viewModel != null) {
                                        // Try to insert students in a separate thread
                                        processStudentsForDatabase(result.getStudents());
                                    }
                                } else {
                                    // No students found
                                    txtUploadStatus.setText("No valid students found in the file");
                                    progressUpload.setIndeterminate(false);
                                    progressUpload.setProgress(0);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "UI thread error: " + e.getMessage(), e);
                                txtUploadStatus.setText("Error: " + e.getMessage());
                            } finally {
                                btnUploadCsv.setEnabled(true);
                                btnSelectCsv.setEnabled(true);
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in background thread: " + e.getMessage(), e);
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            try {
                                txtUploadStatus.setText("Error: " + e.getMessage());
                                Toast.makeText(requireContext(), 
                                    "Error processing file: " + e.getMessage(), 
                                    Toast.LENGTH_LONG).show();
                            } catch (Exception e2) {
                                Log.e(TAG, "Error showing error toast", e2);
                            } finally {
                                btnUploadCsv.setEnabled(true);
                                btnSelectCsv.setEnabled(true);
                                progressUpload.setIndeterminate(false);
                                progressUpload.setProgress(0);
                            }
                        });
                    }
                }
            }).start();
        } catch (Exception e) {
            Log.e(TAG, "Critical error starting upload: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            btnUploadCsv.setEnabled(true);
            btnSelectCsv.setEnabled(true);
            progressUpload.setIndeterminate(false);
            progressUpload.setProgress(0);
        }
    }

    private void processStudentsForDatabase(List<Student> students) {
        if (students == null || students.isEmpty() || viewModel == null) {
            return;
        }
        
        new Thread(() -> {
            try {
                boolean overwriteDuplicates = chkOverwriteDuplicates.isChecked();
                int successCount = 0;
                
                for (Student student : students) {
                    try {
                        boolean success;
                        if (overwriteDuplicates) {
                            success = viewModel.upsertStudent(student);
                        } else {
                            success = viewModel.insertIfNotExists(student);
                        }
                        
                        if (success) {
                            successCount++;
                        }
                        
                        // Small delay to avoid overwhelming the database
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException ignored) {}
                        
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing individual student: " + e.getMessage(), e);
                    }
                }
                
                final int finalSuccessCount = successCount;
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        try {
                            Toast.makeText(requireContext(), 
                                "Saved " + finalSuccessCount + " of " + students.size() + " students to database", 
                                Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Error showing success toast", e);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in database processing thread: " + e.getMessage(), e);
            }
        }).start();
    }

    private void showErrorDialog(List<String> errors) {
        try {
            StringBuilder errorMessage = new StringBuilder();
            if (errors == null || errors.isEmpty()) {
                errorMessage.append("Unknown error occurred while processing the file.");
            } else {
                errorMessage.append("The following issues were found:\n\n");
                
                int displayLimit = Math.min(10, errors.size());
                for (int i = 0; i < displayLimit; i++) {
                    errorMessage.append("• ").append(errors.get(i)).append("\n");
                }
                
                if (errors.size() > displayLimit) {
                    errorMessage.append("\n...and ").append(errors.size() - displayLimit).append(" more issues.");
                }
                
                errorMessage.append("\n\nPlease correct these issues and try again. You can download a sample CSV file using the button below.");
            }
            
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                    .setTitle("CSV Upload Issues")
                    .setMessage(errorMessage.toString())
                    .setPositiveButton("OK", null);
                    
            // Add a download sample button if there are errors
            if (errors != null && !errors.isEmpty()) {
                builder.setNeutralButton("Download Sample", (dialog, which) -> {
                    downloadSampleCsv();
                });
            }
            
            builder.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing error dialog", e);
            // Fallback to simple toast
            Toast.makeText(requireContext(), 
                "Errors found in CSV. Check format and try again.", 
                Toast.LENGTH_LONG).show();
        }
    }

    private void downloadSampleCsv() {
        try {
            // Create a file in the downloads directory
            File downloadsDir = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "");
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }
            
            File outputFile = new File(downloadsDir, "sample_students.csv");
            
            // Check if the assets file exists, if not, create a sample CSV directly
            try {
                // Try to open the assets file
                requireContext().getAssets().open("sample_students.csv");
                
                // If we get here, the file exists, so copy it
                try (InputStream is = requireContext().getAssets().open("sample_students.csv");
                     BufferedInputStream bis = new BufferedInputStream(is);
                     FileOutputStream fos = new FileOutputStream(outputFile)) {
                    
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    fos.flush();
                }
            } catch (IOException e) {
                // File doesn't exist in assets, create it directly
                Log.w(TAG, "Sample CSV not found in assets, creating directly");
                String sampleCsv = "sapId,fullName,email,password,branch,rollNumber,cgpa,gender,backlogs,batch\n" +
                        "500123456,John Doe,john.doe@example.com,pass123,CSE,CSE123,8.5,Male,0,2023\n" +
                        "500123457,Jane Smith,jane.smith@example.com,pass123,ECE,ECE107,8.7,Female,0,2023\n" +
                        "500123458,Alex Johnson,alex.j@example.com,pass123,IT,IT114,7.9,Male,1,2023\n" +
                        "500123459,Sarah Williams,sarah.w@example.com,pass123,CSE,CSE124,9.2,Female,0,2023\n" +
                        "500123460,Michael Brown,michael.b@example.com,pass123,MECH,MECH105,8.1,Male,2,2023";
                
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    fos.write(sampleCsv.getBytes());
                    fos.flush();
                }
            }
            
            // Create a URI using FileProvider
            Uri fileUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    outputFile);
            
            // Show success message with file path
            Toast.makeText(requireContext(), "Sample CSV saved to: " + outputFile.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
            
            // Optionally open the file with an intent
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "text/csv");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            
            try {
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "No app to view CSV", e);
                Toast.makeText(requireContext(), "No app found to view CSV files", Toast.LENGTH_SHORT).show();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error saving sample", e);
            Toast.makeText(requireContext(), "Error saving sample: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showCsvInfoDialog() {
        try {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_csv_info, null);
            AlertDialog dialog = new AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .create();
            
            Button btnClose = dialogView.findViewById(R.id.btn_close_dialog);
            btnClose.setOnClickListener(v -> dialog.dismiss());
            
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing CSV info dialog", e);
            Toast.makeText(requireContext(), "Error showing info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the result from the file picker activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PERMISSION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                handleCsvFileSelected(data.getData());
            } else {
                Toast.makeText(requireContext(), "Failed to get file", Toast.LENGTH_SHORT).show();
            }
        }
    }
} 