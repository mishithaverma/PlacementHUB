package com.example.placementhub.ui.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.placementhub.R;

public class LandingFragment extends Fragment {
    private static final String TAG = "LandingFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_landing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");
        
        try {
            Button studentLoginButton = view.findViewById(R.id.student_login_button);
            Button placementLoginButton = view.findViewById(R.id.placement_login_button);
            
            studentLoginButton.setOnClickListener(v -> {
                Log.d(TAG, "Student login button clicked");
                Navigation.findNavController(v).navigate(R.id.action_landing_to_student_login);
            });
            
            placementLoginButton.setOnClickListener(v -> {
                Log.d(TAG, "Placement login button clicked");
                Navigation.findNavController(v).navigate(R.id.action_landing_to_placement_login);
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated: " + e.getMessage(), e);
        }
    }
} 