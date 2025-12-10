package com.school.management.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.school.management.R;
import com.school.management.activities.MainActivity;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "school_management_messages";
    private static final String CHANNEL_NAME = "School Messages";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "Message received from: " + remoteMessage.getFrom());

        // Handle notification message
        if (remoteMessage.getNotification() != null) {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            String title = notification.getTitle();
            String body = notification.getBody();

            Log.d(TAG, "Notification Title: " + title);
            Log.d(TAG, "Notification Body: " + body);

            // Show notification
            showNotification(title, body, remoteMessage.getData());
        }

        // Handle data message (for in-app messaging)
        if (!remoteMessage.getData().isEmpty()) {
            String messageType = remoteMessage.getData().get("type");
            String senderId = remoteMessage.getData().get("senderId");
            String senderName = remoteMessage.getData().get("senderName");
            String content = remoteMessage.getData().get("content");
            String subject = remoteMessage.getData().get("subject");

            Log.d(TAG, "Data Message Type: " + messageType);

            // Handle based on message type
            if ("message".equals(messageType)) {
                handleInAppMessage(senderId, senderName, subject, content);
            } else if ("notification".equals(messageType)) {
                handleInAppNotification(senderName, content);
            } else if ("announcement".equals(messageType)) {
                handleAnnouncement(senderName, content);
            }
        }
    }

    /**
     * Handle in-app message received
     */
    private void handleInAppMessage(String senderId, String senderName, String subject, String content) {
        try {
            Log.d(TAG, "In-App Message from " + senderName + ": " + subject);

            // Show in-app toast/dialog (you can enhance this with a dialog)
            String message = senderName + ": " + subject;
            // This would require posting to a live data or using a handler
            // For now, we'll show a notification
            showNotification(senderName, subject + " - " + content, null);
        } catch (Exception e) {
            Log.e(TAG, "Error handling in-app message", e);
        }
    }

    /**
     * Handle in-app notification
     */
    private void handleInAppNotification(String senderName, String content) {
        try {
            Log.d(TAG, "In-App Notification from " + senderName);
            showNotification("Notification", content, null);
        } catch (Exception e) {
            Log.e(TAG, "Error handling in-app notification", e);
        }
    }

    /**
     * Handle announcement
     */
    private void handleAnnouncement(String senderName, String content) {
        try {
            Log.d(TAG, "Announcement from " + senderName);
            showNotification("Announcement", content, null);
        } catch (Exception e) {
            Log.e(TAG, "Error handling announcement", e);
        }
    }

    /**
     * Show notification in system tray
     */
    private void showNotification(String title, String body, Map<String, String> data) {
        try {
            // Create notification channel for Android 8+
            createNotificationChannel();

            // Create intent to open app when notification is tapped
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add data to intent if available
            if (data != null && !data.isEmpty()) {
                for (String key : data.keySet()) {
                    intent.putExtra(key, data.get(key));
                }
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // Build notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setContentIntent(pendingIntent);

            // Set large text for expanded view
            if (body != null && body.length() > 50) {
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
            }

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                // Use unique ID for each notification
                int notificationId = (int) System.currentTimeMillis();
                notificationManager.notify(notificationId, builder.build());

                Log.d(TAG, "Notification shown with ID: " + notificationId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification", e);
        }
    }

    /**
     * Create notification channel for Android 8+
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Receive school management messages and announcements");
                channel.enableLights(true);
                channel.enableVibration(true);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                    Log.d(TAG, "Notification channel created");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error creating notification channel", e);
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "FCM Token: " + token);

        // Save token to Firestore for user
        // This would be done in a separate method to update the user's FCM token
        saveFCMTokenToFirestore(token);
    }

    /**
     * Save FCM token to Firestore (implement based on your needs)
     */
    private void saveFCMTokenToFirestore(String token) {
        try {
            Log.d(TAG, "FCM Token saved: " + token.substring(0, 20) + "...");
            // TODO: Save token to Firestore under users collection
            // This allows sending targeted messages to specific users
        } catch (Exception e) {
            Log.e(TAG, "Error saving FCM token", e);
        }
    }
}
