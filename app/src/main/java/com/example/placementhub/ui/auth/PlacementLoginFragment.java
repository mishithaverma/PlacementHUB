package com.example.placementhub.ui.auth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.placementhub.R;

public class PlacementLoginFragment extends Fragment {
    private static final String TAG = "PlacementLoginFragment";
    private static final String AUTHORIZED_EMAIL = "placement@college.edu";
    private static final String AUTHORIZED_PASSWORD = "placement123";
    
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_placement_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");
        
        try {
            // Initialize views
            emailEditText = view.findViewById(R.id.email_edit_text);
            passwordEditText = view.findViewById(R.id.password_edit_text);
            loginButton = view.findViewById(R.id.login_button);
            
            // For development ease, uncomment these lines for auto-filled credentials
            // emailEditText.setText(AUTHORIZED_EMAIL);
            // passwordEditText.setText(AUTHORIZED_PASSWORD);
            
            // Set click listener for login button
            loginButton.setOnClickListener(v -> {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Show loading state
                showLoading(true);
                
                // Simulate network delay for a better UX
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    // Check if credentials match authorized values
                    if (email.equals(AUTHORIZED_EMAIL) && password.equals(AUTHORIZED_PASSWORD)) {
                        Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigate(R.id.action_placement_login_to_placement_dashboard);
                    } else {
                        showLoading(false);
                        Toast.makeText(requireContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }, 800); // Delay for better UX
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error initializing login screen", Toast.LENGTH_SHORT).show();
        }
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