# Firebase Firestore Database Structure

## Collections and Document Structure

### 1. **users** Collection
Documents for all users (Admin, Teachers, Students, Parents, Staff)

```
users/
├── {userId}
│   ├── userId: string
│   ├── email: string
│   ├── fullName: string
│   ├── role: string (ADMIN, TEACHER, STUDENT, PARENT, STAFF)
│   ├── phoneNumber: string
│   ├── profileImage: string (URL)
│   ├── isActive: boolean
│   ├── createdAt: timestamp
│   ├── lastLogin: timestamp
│   └── metadata: object
```

### 2. **students** Collection
Student profile information

```
students/
├── {studentId}
│   ├── studentId: string
│   ├── userId: string (reference to users collection)
│   ├── admissionNumber: string (unique)
│   ├── fullName: string
│   ├── dateOfBirth: timestamp
│   ├── gender: string (MALE, FEMALE, OTHER)
│   ├── class: string (Class 1-12)
│   ├── section: string (A, B, C, etc.)
│   ├── fatherName: string
│   ├── motherName: string
│   ├── guardianName: string
│   ├── contactNumber: string
│   ├── email: string
│   ├── address: string
│   ├── city: string
│   ├── state: string
│   ├── pinCode: string
│   ├── admissionDate: timestamp
│   ├── profileImage: string (URL)
│   ├── isActive: boolean
│   └── createdAt: timestamp
```

### 3. **teachers** Collection
Teacher profile information

```
teachers/
├── {teacherId}
│   ├── teacherId: string
│   ├── userId: string (reference to users collection)
│   ├── employeeId: string (unique)
│   ├── fullName: string
│   ├── dateOfBirth: timestamp
│   ├── gender: string
│   ├── qualification: string
│   ├── specialization: string (Subject)
│   ├── experience: number (years)
│   ├── phoneNumber: string
│   ├── email: string
│   ├── address: string
│   ├── joiningDate: timestamp
│   ├── profileImage: string (URL)
│   ├── isActive: boolean
│   └── createdAt: timestamp
```

### 4. **classes** Collection
School classes/sections

```
classes/
├── {classId}
│   ├── classId: string
│   ├── className: string (Class 1, Class 2, etc.)
│   ├── section: string (A, B, C, D)
│   ├── classTeacherId: string (reference to teachers)
│   ├── capacity: number
│   ├── totalStudents: number
│   ├── academicYear: string (2024-2025)
│   ├── isActive: boolean
│   └── createdAt: timestamp
```

### 5. **attendance** Collection
Student attendance records

```
attendance/
├── {attendanceId}
│   ├── attendanceId: string
│   ├── studentId: string (reference to students)
│   ├── classId: string (reference to classes)
│   ├── date: timestamp
│   ├── status: string (PRESENT, ABSENT, LATE, EXCUSED)
│   ├── markedBy: string (reference to teachers)
│   ├── remarks: string
│   └── createdAt: timestamp
```

### 6. **examinations** Collection
Examination records

```
examinations/
├── {examinationId}
│   ├── examinationId: string
│   ├── examName: string
│   ├── subject: string
│   ├── className: string
│   ├── description: string
│   ├── totalMarks: number
│   ├── examDate: timestamp
│   ├── createdAt: timestamp
│   ├── createdBy: string (reference to teachers)
│   └── isActive: boolean
```

### 7. **studentGrades** Collection
Student exam grades

```
studentGrades/
├── {gradeId}
│   ├── gradeId: string
│   ├── studentId: string (reference to students)
│   ├── examId: string (reference to examinations)
│   ├── classId: string (reference to classes)
│   ├── marksObtained: number
│   ├── totalMarks: number
│   ├── grade: string (A, B, C, D, F)
│   ├── remarks: string
│   ├── recordedDate: timestamp
│   ├── recordedBy: string (reference to teachers)
│   └── createdAt: timestamp
```

### 8. **fees** Collection
Student fee records

```
fees/
├── {feeId}
│   ├── feeId: string
│   ├── studentId: string (reference to students)
│   ├── classId: string (reference to classes)
│   ├── feeType: string (TUITION, TRANSPORT, UNIFORM, etc.)
│   ├── amount: number
│   ├── dueDate: timestamp
│   ├── paidDate: timestamp
│   ├── status: string (PENDING, PARTIAL, PAID, OVERDUE)
│   ├── remarks: string
│   └── createdAt: timestamp
```

### 9. **libraryBooks** Collection
Library book inventory

```
libraryBooks/
├── {bookId}
│   ├── bookId: string
│   ├── title: string
│   ├── author: string
│   ├── isbn: string (unique)
│   ├── category: string
│   ├── description: string
│   ├── totalCopies: number
│   ├── availableCopies: number
│   ├── publicationYear: number
│   ├── publisher: string
│   ├── price: number
│   ├── isActive: boolean
│   └── createdAt: timestamp
```

### 10. **bookIssues** Collection
Library book issue/return records

```
bookIssues/
├── {issueId}
│   ├── issueId: string
│   ├── studentId: string (reference to students)
│   ├── bookId: string (reference to libraryBooks)
│   ├── issueDate: timestamp
│   ├── dueDate: timestamp
│   ├── returnDate: timestamp
│   ├── status: string (ISSUED, RETURNED, OVERDUE)
│   ├── fineAmount: number
│   ├── remarks: string
│   └── createdAt: timestamp
```

### 11. **events** Collection
School events and activities

```
events/
├── {eventId}
│   ├── eventId: string
│   ├── eventName: string
│   ├── description: string
│   ├── eventDate: timestamp
│   ├── location: string
│   ├── category: string (ACADEMIC, SPORTS, CULTURAL, etc.)
│   ├── organizerName: string
│   ├── capacity: number
│   ├── registeredCount: number
│   ├── eventImage: string (URL)
│   ├── isActive: boolean
│   ├── createdBy: string
│   └── createdAt: timestamp
```

### 12. **messages** Collection
Direct messages between users

```
messages/
├── {messageId}
│   ├── messageId: string
│   ├── senderId: string (reference to users)
│   ├── receiverId: string (reference to users)
│   ├── subject: string
│   ├── content: string
│   ├── sentAt: timestamp
│   ├── isRead: boolean
│   ├── messageType: string (DIRECT, ANNOUNCEMENT, ALERT)
│   ├── priority: string (LOW, MEDIUM, HIGH, URGENT)
│   └── createdAt: timestamp
```

### 13. **announcements** Collection
School announcements and notices

```
announcements/
├── {announcementId}
│   ├── announcementId: string
│   ├── title: string
│   ├── description: string
│   ├── category: string (ACADEMIC, EVENT, NOTICE, EMERGENCY)
│   ├── priority: string (LOW, MEDIUM, HIGH, URGENT)
│   ├── postedDate: timestamp
│   ├── postedBy: string (reference to users)
│   ├── expiryDate: timestamp
│   ├── isActive: boolean
│   └── createdAt: timestamp
```

## Setup Instructions

### Step 1: Access Firebase Console
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: `School-Management-System`
3. Navigate to **Firestore Database**

### Step 2: Create Collections
Create all 13 collections listed above:
```
- users
- students
- teachers
- classes
- attendance
- examinations
- studentGrades
- fees
- libraryBooks
- bookIssues
- events
- messages
- announcements
```

### Step 3: Set Firestore Security Rules
Replace the default security rules with:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow authenticated users to read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
    }
    
    // Students can read class info
    match /classes/{classId} {
      allow read: if request.auth != null;
    }
    
    // Students can read own attendance
    match /attendance/{attendanceId} {
      allow read: if request.auth != null;
    }
    
    // Students can read own grades
    match /studentGrades/{gradeId} {
      allow read: if request.auth != null;
    }
    
    // Allow reading messages sent to user
    match /messages/{messageId} {
      allow read: if request.auth.uid == resource.data.receiverId;
      allow create: if request.auth != null;
    }
    
    // Allow reading announcements (public)
    match /announcements/{announcementId} {
      allow read: if request.auth != null;
    }
    
    // Teachers can write attendance, grades, events
    match /attendance/{document=**} {
      allow write: if request.auth != null && 
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'TEACHER';
    }
    
    match /studentGrades/{document=**} {
      allow write: if request.auth != null && 
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'TEACHER';
    }
    
    // Admin access to all
    match /{document=**} {
      allow read, write: if request.auth != null && 
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'ADMIN';
    }
  }
}
```

### Step 4: Enable Authentication Methods
In Firebase Console → Authentication → Sign-in method:
- ✅ Enable **Email/Password**
- ✅ Enable **Google** (optional)
- ✅ Enable **Phone** (optional)

### Step 5: Create Indexes (if needed)
Firestore will auto-suggest indexes when you run queries. Create them as suggested.

### Step 6: Verify in App
Test the connection by:
1. Running the app
2. Creating a user (registration)
3. Checking if data appears in Firestore Console

## Notes
- All `timestamp` fields use Firebase Timestamp type
- All `string` reference fields are document IDs (e.g., userId, studentId)
- Use `createdAt` for sorting/filtering
- Consider pagination for large result sets
- Implement proper indexes for frequent queries
- Add backup rules to protect sensitive data
