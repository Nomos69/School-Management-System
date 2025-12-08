package com.school.management.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Message {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String subject;
    private String content;
    private Date sentAt;
    private boolean isRead;
    private String messageType; // DIRECT, ANNOUNCEMENT, ALERT
    private String priority; // LOW, MEDIUM, HIGH, URGENT

    public Message() {
        // Required for Firebase
    }

    public Message(String messageId, String senderId, String receiverId, String subject, String content) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.subject = subject;
        this.content = content;
        this.sentAt = new Date();
        this.isRead = false;
        this.messageType = "DIRECT";
        this.priority = "MEDIUM";
    }

    // Getters and Setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getSentAt() { return sentAt; }
    public void setSentAt(Date sentAt) { this.sentAt = sentAt; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("senderId", senderId);
        map.put("receiverId", receiverId);
        map.put("subject", subject);
        map.put("content", content);
        map.put("sentAt", sentAt);
        map.put("isRead", isRead);
        map.put("messageType", messageType);
        map.put("priority", priority);
        return map;
    }
}
