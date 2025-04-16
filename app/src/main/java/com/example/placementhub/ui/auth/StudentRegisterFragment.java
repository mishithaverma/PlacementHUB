package com.example.placementhub.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import java.util.UUID;
import java.util.concurrent.Executors;
import com.example.placementhub.R;
import com.example.placementhub.data.database.AppDatabase;
import com.example.placementhub.data.models.Student;

public class StudentRegisterFragment extends Fragment {
    private static final String TAG = "StudentRegisterFragment";
    
    private EditText fullNameEditText;
    private EditText branchEditText;
    private EditText rollNumberEditText;
    private EditText sapIdEditText;
    private EditText cgpaEditText;
    private RadioGroup genderRadioGroup;
    private EditText backlogsEditText;
    private EditText batchEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;
    
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_student_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");
        
        try {
            // Initialize database
            db = AppDatabase.getDatabase(requireContext());
            
            // Initialize views
            initViews(view);
            
            // Set click listener for register button
            registerButton.setOnClickListener(v -> {
                if (validateForm()) {
                    checkEmailAvailability();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error initializing registration screen", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void initViews(View view) {
        fullNameEditText = view.findViewById(R.id.full_name_edit_text);
        branchEditText = view.findViewById(R.id.branch_edit_text);
        rollNumberEditText = view.findViewById(R.id.roll_number_edit_text);
        sapIdEditText = view.findViewById(R.id.sap_id_edit_text);
        cgpaEditText = view.findViewById(R.id.cgpa_edit_text);
        genderRadioGroup = view.findViewById(R.id.gender_radio_group);
        backlogsEditText = view.findViewById(R.id.backlogs_edit_text);
        batchEditText = view.findViewById(R.id.batch_edit_text);
        emailEditText = view.findViewById(R.id.email_edit_text);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        confirmPasswordEditText = view.findViewById(R.id.confirm_password_edit_text);
        registerButton = view.findViewById(R.id.register_button);
    }
    
    private boolean validateForm() {
        // Get all input values
        String fullName = fullNameEditText.getText().toString().trim();
        String branch = branchEditText.getText().toString().trim();
        String rollNumber = rollNumberEditText.getText().toString().trim();
        String sapId = sapIdEditText.getText().toString().trim();
        String cgpaString = cgpaEditText.getText().toString().trim();
        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        String backlogsString = backlogsEditText.getText().toString().trim();
        String batch = batchEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        
        // Validate required fields
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(branch) || 
            TextUtils.isEmpty(rollNumber) || TextUtils.isEmpty(sapId) || 
            TextUtils.isEmpty(cgpaString) || selectedGenderId == -1 || 
            TextUtils.isEmpty(backlogsString) || TextUtils.isEmpty(batch) || 
            TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || 
            TextUtils.isEmpty(confirmPassword)) {
            
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Validate CGPA format
        try {
            float cgpa = Float.parseFloat(cgpaString);
            if (cgpa < 0 || cgpa > 10) {
                Toast.makeText(requireContext(), "CGPA must be between 0 and 10", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid CGPA format", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Validate backlogs as a number
        try {
            int backlogs = Integer.parseInt(backlogsString);
            if (backlogs < 0) {
                Toast.makeText(requireContext(), "Number of backlogs cannot be negative", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid backlogs format", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Validate email format (basic check)
        if (!email.contains("@") || !email.contains(".")) {
            Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Validate password match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Validate password strength (basic check)
        if (password.length() < 6) {
            Toast.makeText(requireContext(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
    
    private void checkEmailAvailability() {
        showLoading(true);
        String email = emailEditText.getText().toString().trim();
        
        LiveData<Student> studentLiveData = db.studentDao().getStudentByEmail(email);
        
        studentLiveData.observe(getViewLifecycleOwner(), new Observer<Student>() {
            @Override
            public void onChanged(Student student) {
                studentLiveData.removeObserver(this); // Remove observer to prevent multiple callbacks
                
                if (student != null) {
                    // Email already exists
                    showLoading(false);
                    Toast.makeText(requireContext(), "Email already registered", Toast.LENGTH_SHORT).show();
                } else {
                    // Email is available, proceed with registration
                    checkSapIdAvailability();
                }
            }
        });
    }
    
    private void checkSapIdAvailability() {
        String sapId = sapIdEditText.getText().toString().trim();
        
        LiveData<Student> studentLiveData = db.studentDao().getStudentById(sapId);
        
        studentLiveData.observe(getViewLifecycleOwner(), new Observer<Student>() {
            @Override
            public void onChanged(Student student) {
                studentLiveData.removeObserver(this); // Remove observer to prevent multiple callbacks
                
                if (student != null) {
                    // SAP ID already exists
                    showLoading(false);
                    Toast.makeText(requireContext(), "SAP ID already registered", Toast.LENGTH_SHORT).show();
                } else {
                    // SAP ID is available, proceed with registration
                    registerStudent();
                }
            }
        });
    }
    
    private void registerStudent() {
        // Get all validated input values
        String fullName = fullNameEditText.getText().toString().trim();
        String branch = branchEditText.getText().toString().trim();
        String rollNumber = rollNumberEditText.getText().toString().trim();
        String sapId = sapIdEditText.getText().toString().trim();
        float cgpa = Float.parseFloat(cgpaEditText.getText().toString().trim());
        
        // Get selected gender
        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedGenderRadio = requireView().findViewById(selectedGenderId);
        String gender = selectedGenderRadio.getText().toString();
        
        int backlogs = Integer.parseInt(backlogsEditText.getText().toString().trim());
        String batch = batchEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        
        // Create a new Student object
        Student student = new Student();
        student.setSapId(sapId);
        student.setFullName(fullName);
        student.setEmail(email);
        student.setPassword(password);
        student.setBranch(branch);
        student.setRollNumber(rollNumber);
        student.setCgpa(cgpa);
        student.setGender(gender);
        student.setBacklogs(backlogs);
        student.setBatch(batch);
        
        // Insert student into database on a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                db.studentDao().insert(student);
                
                // Update UI on main thread
                requireActivity().runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                    // Navigate back to login screen
                    Navigation.findNavController(requireView()).navigate(R.id.action_student_register_to_student_login);
                });
            } catch (Exception e) {
                // Handle database error
                requireActivity().runOnUiThread(() -> {
                    showLoading(false);
                    Log.e(TAG, "Database error: " + e.getMessage(), e);
                    Toast.makeText(requireContext(), "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            registerButton.setEnabled(false);
            registerButton.setText("Registering...");
        } else {
            registerButton.setEnabled(true);
            registerButton.setText("Register");
        }
    }
} 