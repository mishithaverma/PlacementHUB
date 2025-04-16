package com.example.placementhub.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.placementhub.MainActivity;
import com.example.placementhub.R;

/**
 * Worker class for scheduling and showing notifications
 */
public class NotificationWorker extends Worker {
    private static final String TAG = "NotificationWorker";
    private static final String CHANNEL_ID = "placement_hub_channel";
    private static final String CHANNEL_NAME = "Placement Hub Notifications";
    private static final String CHANNEL_DESC = "Notifications for placement drives and updates";
    
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }
    
    @NonNull
    @Override
    public Result doWork() {
        try {
            // Get notification data from input
            String title = getInputData().getString("title");
            String message = getInputData().getString("message");
            String driveId = getInputData().getString("driveId");
            String studentSapId = getInputData().getString("studentSapId");
            
            // If required data is missing, return failure
            if (title == null || message == null) {
                Log.e(TAG, "Missing required notification data");
                return Result.failure();
            }
            
            // Show notification
            showNotification(title, message, driveId);
            
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification: " + e.getMessage(), e);
            return Result.failure();
        }
    }
    
    private void showNotification(String title, String message, String driveId) {
        Context context = getApplicationContext();
        
        // Create notification channel for Android O and above
        createNotificationChannel();
        
        // Create intent for notification click
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("driveId", driveId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                0, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        
        // Show notification
        NotificationManager notificationManager = 
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        if (notificationManager != null) {
            // Use driveId hash as notification ID to avoid duplicate notifications
            int notificationId = driveId != null ? driveId.hashCode() : (int) System.currentTimeMillis();
            notificationManager.notify(notificationId, builder.build());
            
            Log.d(TAG, "Notification shown with ID: " + notificationId);
        }
    }
    
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            
            // Register the channel with the system
            NotificationManager notificationManager = 
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created");
            }
        }
    }
} 