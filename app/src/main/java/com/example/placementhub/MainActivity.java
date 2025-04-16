package com.example.placementhub;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.placementhub.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Log.d(TAG, "Setting content view");
            setContentView(R.layout.activity_main);
            
            // Set up the toolbar as the action bar
            androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
            }
            
            Log.d(TAG, "Finding nav controller");
            FragmentManager fragmentManager = getSupportFragmentManager();
            NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment);
            
            if (navHostFragment != null) {
                navController = navHostFragment.getNavController();
                
                // Set up the app bar configuration
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_landing
                ).build();
                
                NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            } else {
                Log.e(TAG, "NavHostFragment is null");
                Toast.makeText(this, "Navigation initialization error", Toast.LENGTH_LONG).show();
            }
            
            // Check for and request notification permission on Android 13+
            checkNotificationPermission();
            
            // Handle deep linking if needed
            handleIntent(getIntent());
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing application: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("driveId")) {
            String driveId = intent.getStringExtra("driveId");
            Log.d(TAG, "Notification clicked for drive: " + driveId);
            
            // Navigate to appropriate fragment based on deep link data
            // You can implement this based on your app's navigation flow
        }
    }

    // Request notification permission for Android 13+ (API 33+)
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != 
                    PackageManager.PERMISSION_GRANTED) {
                
                // Register the permissions callback
                ActivityResultLauncher<String> requestPermissionLauncher = 
                        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                            if (isGranted) {
                                Log.d(TAG, "Notification permission granted");
                            } else {
                                Log.d(TAG, "Notification permission denied");
                                Toast.makeText(this, 
                                        "Notification permission denied. You may miss important updates.", 
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                
                // Request the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        try {
            return navController.navigateUp() || super.onSupportNavigateUp();
        } catch (Exception e) {
            Log.e(TAG, "Error in onSupportNavigateUp: " + e.getMessage(), e);
            return super.onSupportNavigateUp();
        }
    }
}

