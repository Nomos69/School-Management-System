# Firebase Cloud Messaging (FCM) Integration Guide

## Overview
This guide explains how to use Firebase Cloud Messaging (FCM) for both **notification messages** and **in-app messages** in the School Management System.

## Architecture

### What is FCM?
Firebase Cloud Messaging (FCM) is a cross-platform messaging solution that lets you send messages and notifications to users on Android, iOS, and web.

**Key Features:**
- ✅ Works when app is open (in-app) or closed (notification)
- ✅ Real-time message delivery
- ✅ Topic-based messaging for groups
- ✅ Notification + data payload together
- ✅ Built-in retry logic
- ✅ No need for custom Firestore listeners for real-time notifications

## Implementation Details

### 1. FCMService (Backend Message Handler)
**File:** `com.school.management.services.FCMService`

Handles incoming FCM messages and displays notifications:
```java
public class FCMService extends FirebaseMessagingService {
    - onMessageReceived() - Receives all messages
    - showNotification() - Displays system notification
    - handleInAppMessage() - Processes direct messages
    - handleInAppNotification() - Processes notifications
    - handleAnnouncement() - Processes announcements
}
```

**Message Types Supported:**
1. **Notification Messages** - Show in system tray
2. **Data Messages** - Processed in-app
3. **Combined** - Both notification + data

### 2. MessagingService (Helper Class)
**File:** `com.school.management.services.MessagingService`

Helper service to send messages with both FCM and Firestore:
```java
public class MessagingService {
    - sendMessage() - Send direct message with FCM + Firestore storage
    - sendAnnouncement() - Send announcement to all users
    - sendFCMNotification() - Prepare FCM payload
    - sendBroadcastFCM() - Send to all users
}
```

### 3. MessageRepository (Data Access)
**File:** `com.school.management.repositories.MessageRepository`

Updated to use FCM for real-time notifications:
```java
public void sendMessage(senderId, senderName, receiverId, subject, content, priority)
- Sends message via MessagingService
- Automatically stores in Firestore
- Triggers FCM notification
```

## Message Flow

### Sending a Direct Message
```
Teacher sends message
    ↓
MessageRepository.sendMessage()
    ↓
MessagingService.sendMessage()
    ↓
├─ Save to Firestore (history)
└─ Send FCM notification to student
    ↓
FCMService.onMessageReceived()
    ↓
├─ Show notification in system tray
└─ Process data (mark as read, etc.)
    ↓
Student receives notification
```

### Message Payload
```json
{
  "notification": {
    "title": "Teacher Name",
    "body": "Message Subject"
  },
  "data": {
    "type": "message",
    "senderId": "user123",
    "senderName": "John Teacher",
    "subject": "Assignment Due",
    "content": "Your assignment is due tomorrow",
    "messageId": "msg456"
  }
}
```

## Firebase Setup Required

### Step 1: Get FCM Server Key
1. Go to Firebase Console → Your Project
2. Settings (gear icon) → Project Settings
3. Cloud Messaging tab
4. Copy **Server API Key**

### Step 2: Save FCM Tokens
When app starts, FCMService saves user's FCM token to Firestore:
```
users/{userId}
  ├─ fcmToken: "abc123def456..."
  ├─ fcmTokenUpdated: 2025-12-10
  └─ ...other fields
```

### Step 3: Send Messages from Backend
Use Firebase Admin SDK (Node.js, Python, Go):

**Example (Node.js):**
```javascript
const admin = require('firebase-admin');

// Send to specific user
const message = {
  notification: {
    title: 'Teacher Name',
    body: 'Assignment Due'
  },
  data: {
    type: 'message',
    senderId: 'teacher1',
    senderName: 'John Teacher',
    subject: 'Assignment Due',
    content: 'Your assignment is due tomorrow'
  },
  token: userFCMToken // Get from Firestore
};

await admin.messaging().send(message);
```

**Example (Python):**
```python
from firebase_admin import messaging

message = messaging.MulticastMessage(
    notification=messaging.Notification(
        title='Teacher Name',
        body='Assignment Due'
    ),
    data={
        'type': 'message',
        'senderId': 'teacher1',
        'senderName': 'John Teacher',
        'subject': 'Assignment Due',
        'content': 'Your assignment is due tomorrow'
    },
    tokens=[user_fcm_token]
)

response = messaging.send_multicast(message)
```

## Firestore Collections for FCM

### users Collection
```
users/{userId}
  ├─ email: "student@school.com"
  ├─ name: "John Student"
  ├─ role: "student"
  ├─ fcmToken: "abc123def456..."
  ├─ fcmTokenUpdated: 2025-12-10 10:30:00
  └─ notificationPreferences
      ├─ enabled: true
      ├─ sound: true
      ├─ vibration: true
      └─ priority: "high"
```

### messages Collection
```
messages/{messageId}
  ├─ messageId: "msg123"
  ├─ senderId: "teacher1"
  ├─ receiverId: "student1"
  ├─ subject: "Assignment Due"
  ├─ content: "Your assignment is due tomorrow"
  ├─ sentAt: 2025-12-10 10:30:00
  ├─ isRead: false
  ├─ priority: "HIGH"
  └─ messageType: "DIRECT"
```

### announcements Collection
```
announcements/{announcementId}
  ├─ announcementId: "ann123"
  ├─ senderId: "admin1"
  ├─ title: "Winter Break"
  ├─ content: "School closed from Dec 20 to Jan 5"
  ├─ category: "event"
  ├─ priority: "MEDIUM"
  ├─ sentAt: 2025-12-10
  └─ recipients: ["all"] or ["class-9A"]
```

## Security Rules

### Firestore Rules
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Users can read their own document and FCM token
    match /users/{userId} {
      allow read: if request.auth.uid == userId;
      allow update: if request.auth.uid == userId;
    }
    
    // Users can read messages sent to them
    match /messages/{messageId} {
      allow read: if resource.data.receiverId == request.auth.uid;
      allow create: if request.auth != null; // Created via backend
      allow update: if resource.data.receiverId == request.auth.uid;
    }
    
    // All users can read announcements
    match /announcements/{announcementId} {
      allow read: if request.auth != null;
    }
  }
}
```

## AndroidManifest Permissions

Already added in `AndroidManifest.xml`:
```xml
<!-- Required for FCM -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- FCMService registration -->
<service
    android:name=".services.FCMService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

## Testing FCM

### 1. Using Firebase Console
1. Firebase Console → Messaging → New Campaign
2. Select "Firebase Notification Message"
3. Title: "Test Message"
4. Message: "This is a test"
5. Target: Select your app or audience
6. Send

### 2. Using curl (with Server Key)
```bash
curl -X POST https://fcm.googleapis.com/fcm/send \
  -H "Authorization: key=SERVER_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "notification": {
      "title": "Test Title",
      "body": "Test Body"
    },
    "data": {
      "type": "message"
    },
    "to": "DEVICE_TOKEN"
  }'
```

## Implementation in Activities

### Example: Sending Message from MessagingActivity
```java
private void sendMessage(String receiverId, String subject, String content) {
    messageRepository.sendMessage(
        currentUserId,           // sender ID
        currentUserName,         // sender name
        receiverId,              // recipient
        subject,                 // subject
        content,                 // message content
        "HIGH",                  // priority
        new MessageRepository.OnMessageSentListener() {
            @Override
            public void onSuccess(String messageId) {
                Toast.makeText(MessagingActivity.this, 
                    "Message sent!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(MessagingActivity.this, 
                    "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        }
    );
}
```

## Notification Features

### Priority Levels
- **URGENT** (Red) - Critical, shows heads-up notification
- **HIGH** (Orange) - Important, shows immediately
- **MEDIUM** (Yellow) - Normal priority
- **LOW** (Green) - Can be delayed

### Notification Channels (Android 8+)
```java
NotificationChannel channel = new NotificationChannel(
    "school_management_messages",
    "School Messages",
    NotificationManager.IMPORTANCE_HIGH
);
channel.setDescription("Receive school management messages");
channel.enableLights(true);
channel.enableVibration(true);
notificationManager.createNotificationChannel(channel);
```

## Backend Implementation

### Node.js Cloud Function
```javascript
const admin = require('firebase-admin');

exports.sendMessage = functions.https.onCall(async (data, context) => {
    const { senderId, senderName, receiverId, subject, content, priority } = data;

    // Get recipient's FCM token
    const userDoc = await admin.firestore()
        .collection('users')
        .doc(receiverId)
        .get();

    const fcmToken = userDoc.data().fcmToken;

    // Send FCM notification
    await admin.messaging().send({
        notification: {
            title: senderName,
            body: subject
        },
        data: {
            type: 'message',
            senderId,
            senderName,
            subject,
            content,
            priority
        },
        token: fcmToken
    });

    // Save to Firestore
    await admin.firestore()
        .collection('messages')
        .add({
            senderId,
            receiverId,
            subject,
            content,
            priority,
            sentAt: admin.firestore.FieldValue.serverTimestamp(),
            isRead: false
        });

    return { success: true };
});
```

## Troubleshooting

### FCM Token Not Being Received
1. Check if app has notification permission
2. Verify GoogleServices.json is correct
3. Check Firestore security rules allow token updates

### Notifications Not Showing
1. Verify FCM service is registered in AndroidManifest
2. Check notification channel is created (Android 8+)
3. Verify POST_NOTIFICATIONS permission is granted
4. Check app notification settings in device settings

### Messages Not Delivered
1. Ensure FCM token is saved in Firestore
2. Check backend Server API Key is correct
3. Verify message format is valid JSON
4. Check Firebase Console → Messaging → Reports

## Best Practices

1. **Save FCM Token Securely** - Update on app launch
2. **Handle Token Refresh** - Implement onNewToken()
3. **Test Thoroughly** - Use Firebase Console test sending
4. **Monitor Delivery** - Check Firebase Console reports
5. **Implement Fallbacks** - Have Firestore listener as backup
6. **User Privacy** - Allow users to disable notifications
7. **Rate Limiting** - Don't spam users with messages
8. **Message Segmentation** - Send targeted messages by role/class

## Summary

✅ **FCM handles:**
- Notifications (system tray)
- In-app messages (data payload)
- Both combined
- Real-time delivery
- Offline message queuing

✅ **Firestore handles:**
- Message history
- User data & FCM tokens
- Security rules
- Data persistence

✅ **Backend (Admin SDK) sends:**
- FCM messages to specific users
- Broadcast messages to groups
- Scheduled messages
- Analytics tracking
