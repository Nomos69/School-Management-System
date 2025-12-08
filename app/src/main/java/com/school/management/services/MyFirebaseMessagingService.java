package com.school.management.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.school.management.utils.NotificationHelper;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            
            Log.d(TAG, "Notification Title: " + title);
            Log.d(TAG, "Notification Body: " + body);

            showNotification(title, body);
        }

        // Check if message contains a data payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            handleDataMessage(remoteMessage.getData());
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        // Send token to your server
        sendRegistrationToServer(token);
    }

    private void showNotification(String title, String message) {
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.showNotification(
                com.school.management.utils.Constants.CHANNEL_ID_GENERAL,
                title,
                message,
                (int) System.currentTimeMillis()
        );
    }

    private void handleDataMessage(java.util.Map<String, String> data) {
        String type = data.get("type");
        if (type != null) {
            switch (type) {
                case "attendance":
                    handleAttendanceNotification(data);
                    break;
                case "fee":
                    handleFeeNotification(data);
                    break;
                case "exam":
                    handleExamNotification(data);
                    break;
                case "message":
                    handleMessageNotification(data);
                    break;
                default:
                    break;
            }
        }
    }

    private void handleAttendanceNotification(java.util.Map<String, String> data) {
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.showAttendanceNotification(
                data.get("title"),
                data.get("message")
        );
    }

    private void handleFeeNotification(java.util.Map<String, String> data) {
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.showFeeNotification(
                data.get("title"),
                data.get("message")
        );
    }

    private void handleExamNotification(java.util.Map<String, String> data) {
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.showExamNotification(
                data.get("title"),
                data.get("message")
        );
    }

    private void handleMessageNotification(java.util.Map<String, String> data) {
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.showMessageNotification(
                data.get("title"),
                data.get("message")
        );
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your server
        Log.d(TAG, "sendRegistrationTokenToServer(" + token + ")");
    }
}
