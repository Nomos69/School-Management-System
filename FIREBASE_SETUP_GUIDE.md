# Firebase Setup for School Management App

## Quick Setup Checklist

### Prerequisites
- [ ] Google account
- [ ] Firebase project created
- [ ] Android project connected to Firebase

## Step 1: Connect Android App to Firebase

### 1.1 Download google-services.json
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Click ⚙️ (Settings) → Project Settings
4. Go to **Your apps** section
5. Click on your Android app
6. Download `google-services.json`
7. Place it in: `app/google-services.json`

**Status in this project:** ✅ Already configured

### 1.2 Enable Required Firebase Services
In Firebase Console, enable:
- ✅ **Cloud Firestore** - for database
- ✅ **Authentication** - for user login
- ✅ **Realtime Database** (optional) - for real-time updates
- ✅ **Cloud Storage** (optional) - for profile images
- ✅ **Cloud Messaging** - for notifications (already configured)

## Step 2: Create Firestore Collections

### Required Collections (13 total)

#### Database Structure Diagram
```
Firestore DB
├── users (authentication & profiles)
├── students (student profiles)
├── teachers (teacher profiles)
├── classes (class/section info)
├── attendance (attendance records)
├── examinations (exam info)
├── studentGrades (exam grades)
├── fees (fee records)
├── libraryBooks (book inventory)
├── bookIssues (book borrowing records)
├── events (school events)
├── messages (direct messaging)
└── announcements (school announcements)
```

### Creating Collections in Firebase Console
1. Go to **Cloud Firestore**
2. Click **+ Create collection**
3. Enter collection name (e.g., "users")
4. Click **Next**
5. Add a sample document or start empty
6. Repeat for all 13 collections

### Alternative: Bulk Import
Use Firebase CLI to import collections:
```bash
firebase firestore:import ./firestore-export-dir
```

## Step 3: Configure Security Rules

### Navigate to Security Rules
1. Cloud Firestore → **Rules** tab
2. Replace default rules with content from `FIRESTORE_SCHEMA.md`
3. Click **Publish**

### Key Rules
- Users can only read/write their own data
- Teachers can write grades and attendance
- Students can read their own records
- Announcements are readable by all authenticated users
- Admin has full access

## Step 4: Set Up Authentication

### Email/Password Authentication
1. Go to **Authentication** → **Sign-in method** tab
2. Click **Email/Password**
3. Enable **Email/Password**
4. (Optional) Enable **Email link (passwordless sign-in)**
5. Click **Save**

### Google Sign-In (Optional)
1. Click **Google**
2. Enable it
3. Add project support email
4. Click **Save**

## Step 5: Create Test Users

### Via Firebase Console
1. Go to **Authentication** → **Users** tab
2. Click **+ Add user**
3. Create test accounts:
   - **Admin:** admin@school.com / password123
   - **Teacher:** teacher@school.com / password123
   - **Student:** student@school.com / password123

### Via App Registration
1. Launch the app
2. Go to Register screen
3. Create test users through the app
4. Verify users appear in Authentication → Users tab

## Step 6: Initialize Firestore Data

### Add Sample Data
1. In Firebase Console, click each collection
2. Add documents with sample data
3. Match the schema from `FIRESTORE_SCHEMA.md`

**Example: Add a test class**
```json
{
  "classId": "class_1",
  "className": "Class 1",
  "section": "A",
  "classTeacherId": "teacher_001",
  "capacity": 30,
  "totalStudents": 28,
  "academicYear": "2024-2025",
  "isActive": true,
  "createdAt": "2024-12-10T10:00:00Z"
}
```

## Step 7: Verify App Connection

### Test in App
1. Install app on emulator/device
2. Register a new user
3. Login with that user
4. Check Firebase Console → Firestore → users collection
5. Verify new user document appears

### Check Logs
Monitor logcat for:
- `D/Firestore: Initialize Firestore` - Connection successful
- No auth errors
- No permission denied errors

## Step 8: Configure Cloud Storage (Optional - for Images)

### Enable Cloud Storage
1. Firebase Console → **Storage**
2. Click **Get Started**
3. Configure rules for image uploads

### Rules for Profile Images
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Allow users to upload/download their own profile images
    match /users/{userId}/profile.jpg {
      allow read, write: if request.auth.uid == userId;
    }
  }
}
```

## Step 9: Set Up Cloud Messaging (Already Done)

**Status:** ✅ Already configured in `google-services.json`

Features enabled:
- Push notifications
- FCM tokens for user devices
- Background message handling

## Troubleshooting

### Issue: "Permission denied" errors
**Solution:**
1. Check security rules are published
2. Verify user is authenticated
3. Check user role matches rule requirements
4. Review Firebase Console → Cloud Firestore → Logs tab

### Issue: Data not syncing
**Solution:**
1. Verify internet connection
2. Check Firestore connection in code
3. Review collection names (case-sensitive)
4. Check document IDs exist

### Issue: Authentication fails
**Solution:**
1. Verify email/password auth is enabled
2. Check user exists in Authentication → Users
3. Verify no special characters in passwords
4. Check Firebase project is linked to app

## Performance Optimization

### Indexing
Firestore will suggest indexes for:
- Queries with multiple filters
- Sorting with filters
- Range queries

Create suggested indexes in:
Firebase Console → Cloud Firestore → Indexes

### Query Optimization
```java
// ✅ Good: Single where clause
db.collection("students").whereEqualTo("classId", "class_1").get();

// ⚠️ Requires index: Multiple where clauses
db.collection("students")
  .whereEqualTo("classId", "class_1")
  .whereEqualTo("isActive", true)
  .get();

// ✅ Good: Pagination
db.collection("students").limit(20).get();
```

## Backup & Export

### Enable Automatic Backups
1. Firebase Console → Settings → Backups
2. Enable **Automatic backups**
3. Choose backup location and frequency

### Manual Export
```bash
gcloud firestore export gs://your-bucket/path --collection-ids=users,students
```

## Security Best Practices

- ✅ Never hardcode API keys
- ✅ Use `.gitignore` for `google-services.json`
- ✅ Validate data on server side (security rules)
- ✅ Use timestamps for all records
- ✅ Implement soft deletes (isActive flag)
- ✅ Log important operations
- ✅ Use strong passwords
- ✅ Enable MFA for Firebase Console

## Next Steps

1. ✅ Create all 13 collections
2. ✅ Set up security rules
3. ✅ Configure authentication
4. ✅ Create test users
5. ✅ Add sample data
6. ✅ Test app connection
7. ⏳ Deploy to production
8. ⏳ Monitor Firestore usage

## Support Resources

- [Firebase Documentation](https://firebase.google.com/docs)
- [Cloud Firestore Guide](https://firebase.google.com/docs/firestore)
- [Firebase Security Rules](https://firebase.google.com/docs/firestore/security/start)
- [Firebase Emulator Suite](https://firebase.google.com/docs/emulator-suite) (for testing)
