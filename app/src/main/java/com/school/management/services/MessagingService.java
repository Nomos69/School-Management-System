package com.school.management.services;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.models.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Helper service to send messages via FCM and store in Firestore
 */
public class MessagingService {
    private static final String TAG = "MessagingService";
    private FirebaseFirestore firestore;

    public interface OnMessageSendListener {
        void onSuccess(String messageId);
        void onFailure(String error);
    }

    public MessagingService() {
        firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Send a message via FCM and save to Firestore
     */
    public void sendMessage(String senderId, String senderName, String receiverId, 
                           String subject, String content, String priority, 
                           OnMessageSendListener listener) {
        try {
            String messageId = UUID.randomUUID().toString();

            // Create message object
            Message message = new Message();
            message.setMessageId(messageId);
            message.setSenderId(senderId);
            message.setReceiverId(receiverId);
            message.setSubject(subject);
            message.setContent(content);
            message.setPriority(priority != null ? priority : "MEDIUM");
            message.setMessageType("DIRECT");
            message.setRead(false);

            // Save to Firestore
            saveMessageToFirestore(message, new FirestoreSaveListener() {
                @Override
                public void onSuccess() {
                    // Send FCM notification
                    sendFCMNotification(receiverId, senderName, subject, content, messageId);
                    if (listener != null) listener.onSuccess(messageId);
                }

                @Override
                public void onFailure(String error) {
                    Log.e(TAG, "Failed to save message: " + error);
                    if (listener != null) listener.onFailure("Failed to save message: " + error);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error sending message", e);
            if (listener != null) listener.onFailure(e.getMessage());
        }
    }

    /**
     * Send announcement via FCM and save to Firestore
     */
    public void sendAnnouncement(String senderId, String senderName, String title, 
                                String content, String category, String priority,
                                OnMessageSendListener listener) {
        try {
            String announcementId = UUID.randomUUID().toString();

            // Create data for FCM
            Map<String, String> data = new HashMap<>();
            data.put("type", "announcement");
            data.put("senderId", senderId);
            data.put("senderName", senderName);
            data.put("subject", title);
            data.put("content", content);
            data.put("category", category);
            data.put("priority", priority);

            // Save announcement (this would go to announcements collection)
            firestore.collection("announcements")
                    .document(announcementId)
                    .set(data)
                    .addOnSuccessListener(aVoid -> {
                        // Send FCM to all users (would need to iterate through FCM tokens)
                        sendBroadcastFCM("Announcement: " + title, content, data);
                        if (listener != null) listener.onSuccess(announcementId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to save announcement: " + e.getMessage());
                        if (listener != null) listener.onFailure(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error sending announcement", e);
            if (listener != null) listener.onFailure(e.getMessage());
        }
    }

    /**
     * Save message to Firestore
     */
    private void saveMessageToFirestore(Message message, FirestoreSaveListener listener) {
        try {
            firestore.collection("messages")
                    .document(message.getMessageId())
                    .set(message)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Message saved to Firestore: " + message.getMessageId());
                        if (listener != null) listener.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to save message to Firestore: " + e.getMessage());
                        if (listener != null) listener.onFailure(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error saving message", e);
            if (listener != null) listener.onFailure(e.getMessage());
        }
    }

    /**
     * Send FCM notification to a specific user
     * In production, you would send via Firebase Admin SDK (backend)
     */
    private void sendFCMNotification(String receiverId, String senderName, String subject, 
                                     String content, String messageId) {
        try {
            // Create data payload
            Map<String, String> data = new HashMap<>();
            data.put("type", "message");
            data.put("senderId", receiverId);
            data.put("senderName", senderName);
            data.put("subject", subject);
            data.put("content", content);
            data.put("messageId", messageId);

            Log.d(TAG, "FCM notification would be sent to user: " + receiverId);
            Log.d(TAG, "Subject: " + subject);

            // NOTE: In production, you would send this via Firebase Admin SDK from your backend
            // The backend would:
            // 1. Query the user's FCM token from Firestore
            // 2. Use Admin SDK to send the notification
            // Example (backend Node.js):
            // await admin.messaging().sendMulticast({
            //     tokens: [userFCMToken],
            //     notification: { title: senderName, body: subject },
            //     data: data
            // });
        } catch (Exception e) {
            Log.e(TAG, "Error preparing FCM notification", e);
        }
    }

    /**
     * Send broadcast FCM notification to all users
     * NOTE: This would be implemented on the backend
     */
    private void sendBroadcastFCM(String title, String body, Map<String, String> data) {
        try {
            Log.d(TAG, "Broadcast FCM would be sent: " + title);
            // This would be implemented on Firebase Admin SDK backend
        } catch (Exception e) {
            Log.e(TAG, "Error sending broadcast FCM", e);
        }
    }

    interface FirestoreSaveListener {
        void onSuccess();
        void onFailure(String error);
    }
}
