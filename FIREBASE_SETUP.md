# Firebase Setup Instructions

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Enter project name: "School Management App"
4. Enable Google Analytics (recommended)
5. Select Google Analytics account or create new
6. Click "Create project"

## Step 2: Add Android App

1. In Firebase Console, click Android icon to add Android app
2. Enter details:
   - **Android package name:** `com.school.management`
   - **App nickname:** School Management (optional)
   - **Debug signing certificate SHA-1:** (optional, for Google Sign-In)
3. Click "Register app"

## Step 3: Download Configuration File

1. Download `google-services.json` file
2. Place it in your project at: `app/google-services.json`
3. This file contains all Firebase configuration

## Step 4: Enable Firebase Authentication ⚠️ IMPORTANT

### 4.1 Enable Authentication Service

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: **school-management-app-b2c49**
3. In the left sidebar, click **"Authentication"** (under "Build" section)
4. Click the **"Get started"** button
5. You'll see the Authentication dashboard

### 4.2 Enable Email/Password Sign-In

1. Click the **"Sign-in method"** tab at the top
2. You'll see a list of providers (Google, Facebook, Email/Password, etc.)
3. Click on **"Email/Password"** row
4. Toggle the **"Enable"** switch to ON
5. Click **"Save"** button

### 4.3 Verify Authentication is Enabled

✅ You should see "Email/Password" marked as "Enabled" in the providers list

**Direct Link:** https://console.firebase.google.com/project/school-management-app-b2c49/authentication/providers

### Important Notes:
- The app **WILL NOT WORK** without enabling authentication
- You'll get errors like "Auth provider not enabled" if you skip this step
- Email/Password is the primary authentication method for this app

## Step 5: Create Firestore Database

1. In Firebase Console, go to "Firestore Database"
2. Click "Create database"
3. Select "Start in production mode" (we'll set rules later)
4. Choose Cloud Firestore location (closest to your users)
5. Click "Enable"

## Step 6: Set Firestore Security Rules

Go to "Rules" tab and paste:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Helper function to check if user is logged in
    function isSignedIn() {
      return request.auth != null;
    }
    
    // Helper function to get user role
    function getUserRole() {
      return get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role;
    }
    
    // Helper function to check if user is admin
    function isAdmin() {
      return isSignedIn() && getUserRole() == 'ADMIN';
    }
    
    // Helper function to check if user is teacher
    function isTeacher() {
      return isSignedIn() && getUserRole() == 'TEACHER';
    }
    
    // Users collection
    match /users/{userId} {
      allow read: if isSignedIn();
      allow create: if isAdmin();
      allow update: if isAdmin() || request.auth.uid == userId;
      allow delete: if isAdmin();
    }
    
    // Students collection
    match /students/{studentId} {
      allow read: if isSignedIn();
      allow write: if isAdmin() || isTeacher();
    }
    
    // Teachers collection
    match /teachers/{teacherId} {
      allow read: if isSignedIn();
      allow write: if isAdmin();
    }
    
    // Classes collection
    match /classes/{classId} {
      allow read: if isSignedIn();
      allow write: if isAdmin() || isTeacher();
    }
    
    // Attendance collection
    match /attendance/{attendanceId} {
      allow read: if isSignedIn();
      allow write: if isAdmin() || isTeacher();
    }
    
    // Examinations collection
    match /examinations/{examId} {
      allow read: if isSignedIn();
      allow write: if isAdmin() || isTeacher();
    }
    
    // Grades collection
    match /grades/{gradeId} {
      allow read: if isSignedIn();
      allow write: if isAdmin() || isTeacher();
    }
    
    // Fees collection
    match /fees/{feeId} {
      allow read: if isSignedIn();
      allow write: if isAdmin();
    }
    
    // Library books collection
    match /library_books/{bookId} {
      allow read: if isSignedIn();
      allow write: if isAdmin();
    }
    
    // Book issues collection
    match /book_issues/{issueId} {
      allow read: if isSignedIn();
      allow write: if isAdmin();
    }
    
    // Events collection
    match /events/{eventId} {
      allow read: if isSignedIn();
      allow write: if isAdmin() || isTeacher();
    }
    
    // Messages collection
    match /messages/{messageId} {
      allow read: if isSignedIn() && 
        (resource.data.senderId == request.auth.uid || 
         resource.data.receiverId == request.auth.uid);
      allow create: if isSignedIn();
      allow update: if isSignedIn() && resource.data.receiverId == request.auth.uid;
    }
    
    // Announcements collection
    match /announcements/{announcementId} {
      allow read: if isSignedIn();
      allow write: if isAdmin();
    }
    
    // Audit logs collection
    match /audit_logs/{logId} {
      allow read: if isAdmin();
      allow create: if isSignedIn();
    }
  }
}
```

## Step 7: Enable Firebase Storage

1. In Firebase Console, go to "Storage"
2. Click "Get started"
3. Select "Start in production mode"
4. Click "Next" and "Done"

## Step 8: Set Storage Security Rules

Go to "Rules" tab and paste:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
  }
}
```

## Step 9: Enable Cloud Messaging

1. In Firebase Console, go to "Cloud Messaging"
2. Note the Server Key (for backend if needed)
3. FCM is automatically enabled

## Step 10: Create Initial Admin User

### Using Firebase Console:

1. Go to "Authentication" → "Users"
2. Click "Add user"
3. Enter:
   - Email: `admin@school.com`
   - Password: `Admin@123` (change after first login)
4. Click "Add user"
5. Note the User UID

### In Firestore:

1. Go to "Firestore Database"
2. Click "Start collection"
3. Collection ID: `users`
4. Document ID: [paste the User UID from Authentication]
5. Add fields:
   - `userId` (string): [paste the User UID]
   - `email` (string): `admin@school.com`
   - `fullName` (string): `System Administrator`
   - `role` (string): `ADMIN`
   - `isActive` (boolean): `true`
   - `createdAt` (timestamp): [current date/time]
6. Click "Save"

## Step 11: Build and Run the App

1. Open Android Studio
2. Open the project
3. Wait for Gradle sync to complete
4. Connect Android device or start emulator
5. Click Run button (Shift + F10)
6. App will install and launch

## Step 12: Test Login

1. Open the app
2. Enter email: `admin@school.com`
3. Enter password: `Admin@123`
4. Click Login
5. You should see the main dashboard

## Optional: Add More Test Users

Repeat Step 10 for:
- **Teacher:** teacher@school.com
- **Student:** student@school.com
- **Parent:** parent@school.com

Make sure to set the correct `role` field for each user type.

## Troubleshooting

### App crashes on launch
- Check if `google-services.json` is in the correct location
- Verify package name matches in Firebase and app

### Login fails
- Check Firebase Authentication is enabled
- Verify user exists in both Authentication and Firestore
- Check user `isActive` is `true`

### Data not loading
- Verify Firestore security rules are published
- Check internet connection
- Review Android Studio Logcat for errors

## Next Steps

1. Add more users through the app's user management
2. Create student, teacher, and class records
3. Customize the app branding
4. Set up cloud functions for advanced features
5. Configure email templates in Firebase

## Important Notes

- Keep `google-services.json` secure (added to .gitignore)
- Change default admin password immediately
- Regularly backup Firestore data
- Monitor Firebase usage and costs
- Enable two-factor authentication for Firebase Console

---

**Congratulations! Your School Management App is now connected to Firebase!**
