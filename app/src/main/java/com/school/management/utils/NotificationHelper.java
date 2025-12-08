package com.school.management.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.school.management.R;
import com.school.management.activities.MainActivity;

public class NotificationHelper {
    private Context context;
    private NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // General Notifications
            NotificationChannel generalChannel = new NotificationChannel(
                    Constants.CHANNEL_ID_GENERAL,
                    "General Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            generalChannel.setDescription("General school notifications");
            notificationManager.createNotificationChannel(generalChannel);

            // Attendance Notifications
            NotificationChannel attendanceChannel = new NotificationChannel(
                    Constants.CHANNEL_ID_ATTENDANCE,
                    "Attendance Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            attendanceChannel.setDescription("Attendance related notifications");
            notificationManager.createNotificationChannel(attendanceChannel);

            // Fee Notifications
            NotificationChannel feeChannel = new NotificationChannel(
                    Constants.CHANNEL_ID_FEES,
                    "Fee Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            feeChannel.setDescription("Fee payment notifications");
            notificationManager.createNotificationChannel(feeChannel);

            // Exam Notifications
            NotificationChannel examChannel = new NotificationChannel(
                    Constants.CHANNEL_ID_EXAMS,
                    "Exam Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            examChannel.setDescription("Exam and result notifications");
            notificationManager.createNotificationChannel(examChannel);

            // Message Notifications
            NotificationChannel messageChannel = new NotificationChannel(
                    Constants.CHANNEL_ID_MESSAGES,
                    "Message Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            messageChannel.setDescription("Message notifications");
            notificationManager.createNotificationChannel(messageChannel);
        }
    }

    public void showNotification(String channelId, String title, String message, int notificationId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                0, 
                intent, 
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(notificationId, builder.build());
    }

    public void showAttendanceNotification(String title, String message) {
        showNotification(Constants.CHANNEL_ID_ATTENDANCE, title, message, 1001);
    }

    public void showFeeNotification(String title, String message) {
        showNotification(Constants.CHANNEL_ID_FEES, title, message, 1002);
    }

    public void showExamNotification(String title, String message) {
        showNotification(Constants.CHANNEL_ID_EXAMS, title, message, 1003);
    }

    public void showMessageNotification(String title, String message) {
        showNotification(Constants.CHANNEL_ID_MESSAGES, title, message, 1004);
    }

    public void cancelNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }

    public void cancelAllNotifications() {
        notificationManager.cancelAll();
    }
}
