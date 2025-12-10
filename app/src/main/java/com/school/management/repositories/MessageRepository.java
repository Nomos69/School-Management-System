package com.school.management.repositories;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.school.management.models.Message;
import com.school.management.services.MessagingService;

import java.util.List;

public class MessageRepository {
    private static final String TAG = "MessageRepository";
    private FirebaseFirestore firestore;
    private MessagingService messagingService;

    public interface OnCompleteListener {
        void onSuccess();
        void onFailure(String error);
    }

    public interface OnMessageListListener {
        void onSuccess(List<Message> messages);
        void onFailure(String error);
    }

    public interface OnMessageSentListener {
        void onSuccess(String messageId);
        void onFailure(String error);
    }

    public MessageRepository() {
        firestore = FirebaseFirestore.getInstance();
        messagingService = new MessagingService();
    }

    /**
     * Save a new message via FCM and Firestore
     */
    public void sendMessage(String senderId, String senderName, String receiverId,
                           String subject, String content, String priority,
                           OnMessageSentListener listener) {
        try {
            messagingService.sendMessage(senderId, senderName, receiverId, subject, content, priority,
                    new MessagingService.OnMessageSendListener() {
                        @Override
                        public void onSuccess(String messageId) {
                            Log.d(TAG, "Message sent successfully: " + messageId);
                            if (listener != null) listener.onSuccess(messageId);
                        }

                        @Override
                        public void onFailure(String error) {
                            Log.e(TAG, "Failed to send message: " + error);
                            if (listener != null) listener.onFailure(error);
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error sending message", e);
            if (listener != null) listener.onFailure(e.getMessage());
        }
    }

    /**
     * Save a new message to Firestore (legacy method)
     */
    public void saveMessage(Message message, OnCompleteListener listener) {
        firestore.collection("messages").document(message.getMessageId())
                .set(message)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    /**
     * Get all messages for a recipient
     */
    public void getMessagesForUser(String userId, OnMessageListListener listener) {
        try {
            firestore.collection("messages")
                    .whereEqualTo("receiverId", userId)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            String errorMsg = error.getMessage() != null ? error.getMessage() : "Unknown error";
                            if (listener != null) listener.onFailure("Failed to load messages: " + errorMsg);
                            return;
                        }

                        if (value != null) {
                            try {
                                List<Message> messages = value.toObjects(Message.class);
                                if (listener != null) listener.onSuccess(messages);
                            } catch (Exception e) {
                                if (listener != null) listener.onFailure("Error parsing messages: " + e.getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            if (listener != null) listener.onFailure("Error querying messages: " + e.getMessage());
        }
    }

    /**
     * Get messages between two users
     */
    public void getConversation(String userId1, String userId2, OnMessageListListener listener) {
        try {
            firestore.collection("messages")
                    .whereEqualTo("senderId", userId1)
                    .whereEqualTo("receiverId", userId2)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            String errorMsg = error.getMessage() != null ? error.getMessage() : "Unknown error";
                            if (listener != null) listener.onFailure("Failed to load conversation: " + errorMsg);
                            return;
                        }

                        if (value != null) {
                            try {
                                List<Message> messages = value.toObjects(Message.class);
                                if (listener != null) listener.onSuccess(messages);
                            } catch (Exception e) {
                                if (listener != null) listener.onFailure("Error parsing conversation: " + e.getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            if (listener != null) listener.onFailure("Error querying conversation: " + e.getMessage());
        }
    }

    /**
     * Mark message as read
     */
    public void markAsRead(String messageId, OnCompleteListener listener) {
        firestore.collection("messages").document(messageId)
                .update("isRead", true)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    /**
     * Delete a message
     */
    public void deleteMessage(String messageId, OnCompleteListener listener) {
        firestore.collection("messages").document(messageId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    /**
     * Get unread message count for user
     */
    public void getUnreadCount(String userId, OnCompleteListener listener) {
        try {
            firestore.collection("messages")
                    .whereEqualTo("receiverId", userId)
                    .whereEqualTo("isRead", false)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            String errorMsg = error.getMessage() != null ? error.getMessage() : "Unknown error";
                            if (listener != null) listener.onFailure("Failed to get unread count: " + errorMsg);
                            return;
                        }

                        if (value != null && listener != null) {
                            listener.onSuccess();
                        }
                    });
        } catch (Exception e) {
            if (listener != null) listener.onFailure("Error getting unread count: " + e.getMessage());
        }
    }
}
