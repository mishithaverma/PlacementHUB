package com.example.placementhub.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import com.example.placementhub.R;
import com.example.placementhub.data.database.AppDatabase;
import com.example.placementhub.data.models.Student;
import java.util.concurrent.Executors;

public class StudentLoginFragment extends Fragment {
    private static final String TAG = "StudentLoginFragment";
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerTextView;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_student_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");
        
        try {
            // Initialize database
            db = AppDatabase.getDatabase(requireContext());
            
            // Initialize views
            emailEditText = view.findViewById(R.id.email_edit_text);
            passwordEditText = view.findViewById(R.id.password_edit_text);
            loginButton = view.findViewById(R.id.login_button);
            registerTextView = view.findViewById(R.id.register_text_view);
            
            // Set click listener for login button
            loginButton.setOnClickListener(v -> {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Authenticate with database
                authenticateUser(email, password);
            });
            
            // Set click listener for register text
            registerTextView.setOnClickListener(v -> {
                Log.d(TAG, "Register text clicked");
                Navigation.findNavController(v).navigate(R.id.action_student_login_to_student_register);
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error initializing login screen", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void authenticateUser(String email, String password) {
        showLoading(true);
        LiveData<Student> studentLiveData = db.studentDao().login(email, password);
        
        studentLiveData.observe(getViewLifecycleOwner(), new Observer<Student>() {
            @Override
            public void onChanged(Student student) {
                showLoading(false);
                studentLiveData.removeObserver(this); // Remove observer to prevent multiple callbacks
                
                if (student != null) {
                    // Login successful
                    Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show();
                    
                    // Create bundle with student SAP ID
                    Bundle args = new Bundle();
                    args.putString("student_sap_id", student.getSapId());
                    
                    // Navigate to dashboard with student ID
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_student_login_to_student_dashboard, args);
                } else {
                    // Login failed
                    Toast.makeText(requireContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            loginButton.setEnabled(false);
            loginButton.setText("Logging in...");
        } else {
            loginButton.setEnabled(true);
            loginButton.setText("Login");
        }
    }
} 