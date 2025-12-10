# Quick Reference Guide - Firebase Collections Setup

## 🚀 Fastest Way to Set Up Firebase (5 minutes)

### Step 1: Firebase Console - Collections (1 minute)
Go to [console.firebase.google.com](https://console.firebase.google.com)

Create these 13 collections:
```
users              → For user accounts
students           → For student profiles  
teachers           → For teacher profiles
classes            → For school classes
attendance         → For attendance records
examinations       → For exam information
studentGrades      → For student grades
fees               → For fee records
libraryBooks       → For books
bookIssues         → For book borrowing
events             → For school events
messages           → For direct messages
announcements      → For announcements
```

### Step 2: Add First Document (30 seconds per collection)
Example for "users" collection:

**Document ID:** user_001
```json
{
  "userId": "user_001",
  "email": "admin@school.com",
  "fullName": "School Admin",
  "role": "ADMIN",
  "phoneNumber": "9876543210",
  "isActive": true,
  "createdAt": "2025-12-10T12:00:00Z"
}
```

### Step 3: Security Rules (2 minutes)
Go to **Firestore → Rules** and paste:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Authenticated users can read their own data
    match /{document=**} {
      allow read, write: if request.auth != null && 
        request.auth.uid == resource.data.userId;
    }
    
    // Admins have full access
    match /{document=**} {
      allow read, write: if request.auth != null && 
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'ADMIN';
    }
    
    // Public announcements
    match /announcements/{doc=**} {
      allow read: if request.auth != null;
    }
  }
}
```

### Step 4: Enable Authentication (30 seconds)
1. Go to **Authentication → Sign-in method**
2. Click **Email/Password**
3. Toggle **Enable**
4. Click **Save**

### Step 5: Create Test User (30 seconds)
1. Go to **Authentication → Users**
2. Click **+ Add user**
3. Email: `test@school.com`
4. Password: `password123`
5. Click **Create user**

---

## 📱 Testing in App (2 minutes)

1. **Register:** Use email `test@school.com` / `password123`
2. **Login:** Should work instantly
3. **Check Firestore:** Go to Firebase → Firestore → users collection
4. **Verify:** New user document should appear

---

## 🔗 Field Reference (Copy-Paste)

### Users Collection
```
userId (string)
email (string)
fullName (string)
role (string): ADMIN | TEACHER | STUDENT | PARENT | STAFF
phoneNumber (string)
isActive (boolean)
createdAt (timestamp)
```

### Students Collection
```
studentId (string)
userId (string) - reference to users
admissionNumber (string)
fullName (string)
class (string): Class 1-12
section (string): A, B, C, D
dateOfBirth (timestamp)
fatherName (string)
email (string)
isActive (boolean)
createdAt (timestamp)
```

### Examinations Collection
```
examinationId (string)
examName (string)
subject (string)
className (string)
totalMarks (number)
examDate (timestamp)
description (string)
createdBy (string)
createdAt (timestamp)
isActive (boolean)
```

### StudentGrades Collection
```
gradeId (string)
studentId (string)
examId (string)
marksObtained (number)
totalMarks (number)
grade (string): A, B, C, D, F
recordedDate (timestamp)
recordedBy (string)
createdAt (timestamp)
```

### Messages Collection
```
messageId (string)
senderId (string)
receiverId (string)
subject (string)
content (string)
sentAt (timestamp)
isRead (boolean)
priority (string): LOW | MEDIUM | HIGH | URGENT
messageType (string): DIRECT | ANNOUNCEMENT | ALERT
createdAt (timestamp)
```

### Announcements Collection
```
announcementId (string)
title (string)
description (string)
category (string): ACADEMIC | EVENT | NOTICE | EMERGENCY
priority (string): LOW | MEDIUM | HIGH | URGENT
postedDate (timestamp)
postedBy (string)
expiryDate (timestamp)
isActive (boolean)
createdAt (timestamp)
```

---

## ⚡ Quick Commands

### Check Firestore Connection (Android Studio)
```
adb logcat | grep -i firestore
```

### Clear App Data
```
adb shell pm clear com.school.management
```

### View Firebase Logs
```
Firebase Console → Firestore → Logs tab
```

---

## 🆘 Troubleshooting Checklist

| Problem | Solution |
|---------|----------|
| "Permission denied" | Check security rules are published |
| Data not appearing | Verify collection name (case-sensitive) |
| Can't register user | Verify Email/Password auth is enabled |
| Firestore connection fails | Check google-services.json is in app/ folder |
| No data in Firestore | Add sample documents manually in console |

---

## 📊 Firestore Collection Dependencies

```
users
├── students (references users.userId)
├── teachers (references users.userId)
├── messages (references users.senderId/receiverId)
└── announcements (references users.postedBy)

classes
└── students (references classId)

examinations
├── studentGrades (references examinationId)
└── students (references className)

studentGrades
├── students (references studentId)
└── examinations (references examId)

events
└── users (references organizedBy)

libraryBooks
└── bookIssues (references bookId)

attendance
├── students (references studentId)
├── classes (references classId)
└── users (references markedBy)

fees
└── students (references studentId)
```

---

## ✨ Pro Tips

1. **Always use lowercase** for collection names: `users` not `Users`
2. **Use auto-generated IDs** unless you need specific ones
3. **Add timestamps** to every document for sorting
4. **Use references** (document IDs) not nested objects
5. **Index important queries** in Firestore console
6. **Test security rules** before production
7. **Backup Firestore** regularly
8. **Monitor usage** in Firebase Console

---

## 📞 Support Links

- Firebase Console: https://console.firebase.google.com
- Firestore Docs: https://firebase.google.com/docs/firestore
- Security Rules: https://firebase.google.com/docs/firestore/security/start
- Android Docs: https://developer.android.com/docs

---

**Last Updated:** December 10, 2025  
**Time to Complete:** ~15 minutes
