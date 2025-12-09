# Admin Login Troubleshooting Guide

## Problem: Cannot Login with Admin Credentials

If you cannot login with the admin account even though the credentials are correct, follow these steps:

## Quick Fix Steps

### Step 1: Access Admin Setup Tool
1. Open the app
2. Go to the Login screen
3. **Tap the "Login" title 5 times quickly**
4. The Admin Setup screen will open

### Step 2: Check Admin Status
1. Click "Check Admin User" button
2. The tool will check:
   - ✅ If admin exists in Firebase Authentication
   - ✅ If admin document exists in Firestore
   - ✅ If admin account is active

### Step 3: Common Issues & Fixes

#### Issue 1: Admin NOT in Firebase Auth
**Symptom:** "❌ Admin NOT found in Firebase Auth"

**Fix:**
1. Click "Create/Fix Admin User" button
2. This will create the admin account with:
   - Email: `admin@school.com`
   - Password: `Admin@123`

#### Issue 2: Admin exists in Auth but NOT in Firestore
**Symptom:** "✅ Admin exists in Firebase Auth" but "❌ Admin document NOT found in Firestore"

**Fix:**
1. Click "Create/Fix Admin User" button
2. This will create the missing Firestore document

#### Issue 3: Admin account is DEACTIVATED
**Symptom:** "⚠️ WARNING: Admin account is DEACTIVATED!"

**Fix Options:**

**Option A: Using Admin Setup Tool**
1. Click "Create/Fix Admin User" to reactivate
2. The tool will set `isActive: true`

**Option B: Using Firebase Console**
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Open your project: `school-management-app-b2c49`
3. Go to Firestore Database
4. Find collection: `users`
5. Find document with `email: admin@school.com`
6. Edit the document
7. Set `isActive` field to `true`
8. Save changes

### Step 4: Test Login
1. In Admin Setup screen, click "Test Admin Login"
2. If you see "✅ LOGIN WILL WORK!", return to login screen
3. Login with:
   - Email: `admin@school.com`
   - Password: `Admin@123`

## Manual Setup (Alternative Method)

If the tool doesn't work, you can manually create the admin account:

### 1. Create User in Firebase Authentication

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: `school-management-app-b2c49`
3. Go to **Authentication** → **Users**
4. Click **"Add user"**
5. Enter:
   - **Email:** `admin@school.com`
   - **Password:** `Admin@123` (or your preferred password)
6. Click **"Add user"**
7. **Copy the User UID** (you'll need this)

### 2. Create Document in Firestore

1. In Firebase Console, go to **Firestore Database**
2. Click on the **"users"** collection (or create it if it doesn't exist)
3. Click **"Add document"**
4. **Document ID:** Paste the User UID you copied
5. Add these fields:

| Field Name | Type | Value |
|------------|------|-------|
| userId | string | [paste User UID] |
| email | string | admin@school.com |
| fullName | string | System Administrator |
| role | string | ADMIN |
| isActive | boolean | true |
| createdAt | timestamp | [current date/time] |
| phoneNumber | string | [leave empty or add phone] |
| profileImageUrl | string | [leave empty] |
| permissions | map | {} (empty map) |

6. Click **"Save"**

### 3. Verify Setup

1. Open the app
2. Try logging in with:
   - Email: `admin@school.com`
   - Password: `Admin@123`

## Debugging with Logcat

To see detailed error messages:

1. Connect your Android device via USB
2. Enable USB Debugging on your device
3. Run: `adb logcat | grep -i "AuthRepository\|LoginActivity"`
4. Try logging in
5. Check the logs for error messages

Common log messages:
- "Login failed" → Wrong password or user doesn't exist in Auth
- "User document does not exist" → Missing Firestore document
- "User isActive is false" → Account is deactivated

## Firebase Console Links

- **Project:** https://console.firebase.google.com/project/school-management-app-b2c49
- **Authentication:** https://console.firebase.google.com/project/school-management-app-b2c49/authentication/users
- **Firestore:** https://console.firebase.google.com/project/school-management-app-b2c49/firestore

## Security Note

⚠️ **IMPORTANT:** After successfully logging in with the default password (`Admin@123`), immediately:
1. Go to Profile/Settings
2. Change your password to something secure
3. Use a strong password with:
   - At least 8 characters
   - Mix of uppercase and lowercase
   - Numbers and special characters

## Still Having Issues?

Check the app logs for detailed error messages. The updated `AuthRepository` now includes detailed logging:
- Firebase Auth status
- User UID
- Firestore document existence
- Account active status
- Specific error messages

Look for these tags in Logcat:
- `AuthRepository`
- `LoginActivity`
