# Examination Module - Quick Reference Guide

## Activities Overview

### 1. ExamListActivity (com.school.management.activities.examination)
**Purpose**: Display list of all examinations  
**Features**:
- List all exams with dates, marks, and status
- Filter by exam date
- Click to view exam details
- FAB to add new examination

**Key Methods**:
- `loadExaminations()` - Fetches exams from Firestore
- `onExaminationClick()` - Handles exam selection

### 2. AddExaminationActivity (com.school.management.activities.examinations)
**Purpose**: Create new examinations  
**Features**:
- Input exam name, subject, marks, description
- Date picker for exam date
- Class selector
- Save to Firestore

**Key Methods**:
- `showDatePicker()` - Opens date picker
- `saveExamination()` - Saves exam to database

### 3. ExaminationDetailActivity (com.school.management.activities.examinations)
**Purpose**: View and manage exam details  
**Features**:
- Display exam information
- Show exam status (Upcoming/Completed)
- Delete exam functionality

**Key Methods**:
- `loadExaminationDetails()` - Fetches exam data
- `displayExaminationDetails()` - Displays data in UI
- `deleteExamination()` - Deletes exam with confirmation

### 4. GradebookActivity (com.school.management.activities.examination)
**Purpose**: Manage student grades for exams  
**Features**:
- Select exam and class
- View all students in the class
- Enter/edit marks for each student
- Auto-calculate letter grades
- Real-time save to Firestore

**Key Methods**:
- `setupSpinners()` - Initializes exam/class selectors
- `loadGrades()` - Fetches grades for selected exam/class
- `onGradeEdit()` - Saves grade to database
- `loadStudentsAndCreateGrades()` - Creates grade entries for new exams

### 5. ReportCardActivity (com.school.management.activities.examination)
**Purpose**: Generate and view student report cards  
**Features**:
- Select student and academic year
- Display all exams and grades in table
- Calculate overall percentage
- Show grade letters
- Professional report card layout

**Key Methods**:
- `loadReportCard()` - Fetches student grades
- `displayReportCard()` - Generates report card UI
- `loadStudentsForSpinner()` - Populates student dropdown
- `loadAcademicYearsForSpinner()` - Populates year dropdown

## Models

### Examination.java
**Key Properties**:
- `examId`, `examinationId` - Unique exam ID
- `examName` - Exam title
- `examType` - QUIZ, MIDTERM, FINAL, PRACTICAL
- `classId`, `className` - Class information
- `subjectId`, `subject` - Subject information
- `examDate`, `examDateMillis` - Exam date (both formats)
- `totalMarks`, `passingMarks` - Mark information
- `duration` - Exam duration in minutes
- `description` - Exam description
- `academicYear`, `term` - Academic period
- `isPublished` - Publication status
- `createdAt`, `updatedAt` - Timestamps

**Key Methods**:
- `toMap()` - Convert to Firestore document
- All standard getters/setters

### StudentGrade.java
**Key Properties**:
- `studentId`, `studentName` - Student information
- `examId`, `classId` - Exam/Class reference
- `marksObtained`, `totalMarks` - Marks
- `grade` - Letter grade (A-F)
- `remarks` - Additional notes
- `recordedDate`, `recordedBy` - Audit trail

**Key Methods**:
- `calculateGrade(int obtained, int total)` - Auto-calculate letter grade
- `toMap()` - Convert to Firestore document

## Adapters

### ExaminationAdapter
**Usage**: Display list of examinations  
**Features**:
- Shows exam name, subject, date, marks, class
- Status indicator (Upcoming/Completed)
- Click listener for item selection
- Color-coded status display

### GradebookAdapter
**Usage**: Display editable list of student grades  
**Features**:
- Student name display
- Editable marks field
- Auto-calculated grade display
- Real-time updates via listener
- TextWatcher for input validation

## Repository

### ExaminationRepository
**Purpose**: Handle all Firestore operations  

**Key Methods**:
- `saveExamination(Examination, listener)` - Save exam
- `getExamination(examId, listener)` - Fetch single exam
- `getExaminationsByClass(classId, listener)` - Fetch class exams
- `getAllExaminations(listener)` - Fetch all exams
- `deleteExamination(examId, listener)` - Delete exam
- `saveStudentGrade(StudentGrade, listener)` - Save grade
- `getGradesForExam(examId, classId, listener)` - Fetch grades
- `getStudentReportCard(studentId, listener)` - Fetch student grades

## Firestore Collections

### /examinations
```json
{
  "examId": "string",
  "examName": "string",
  "examType": "QUIZ|MIDTERM|FINAL|PRACTICAL",
  "classId": "string",
  "className": "string",
  "subjectId": "string",
  "subject": "string",
  "totalMarks": 100,
  "passingMarks": 40,
  "duration": 60,
  "examDate": Timestamp,
  "examDateMillis": 1733800000000,
  "description": "string",
  "academicYear": "2024-2025",
  "term": "Term 1",
  "isPublished": true,
  "createdAt": 1733800000000,
  "updatedAt": 1733800000000
}
```

### /studentGrades
**Document ID**: `{studentId}_{examId}`
```json
{
  "studentId": "string",
  "studentName": "string",
  "examId": "string",
  "classId": "string",
  "marksObtained": 85,
  "totalMarks": 100,
  "grade": "A",
  "remarks": "string",
  "recordedDate": 1733800000000,
  "recordedBy": "teacher_id"
}
```

## Usage Examples

### Adding a New Examination
```java
Intent intent = new Intent(context, AddExaminationActivity.class);
startActivity(intent);
```

### Viewing Examination List
```java
Intent intent = new Intent(context, ExamListActivity.class);
startActivity(intent);
```

### Managing Grades (Gradebook)
```java
Intent intent = new Intent(context, GradebookActivity.class);
startActivity(intent);
```

### Viewing Report Card
```java
Intent intent = new Intent(context, ReportCardActivity.class);
startActivity(intent);
```

## Common Workflows

### Workflow 1: Create and Publish Exam
1. Open ExamListActivity
2. Click FAB to add examination
3. Fill exam details (name, subject, date, marks, etc.)
4. Save exam
5. Exam appears in list

### Workflow 2: Enter Student Grades
1. Open GradebookActivity
2. Select exam from spinner
3. Select class from spinner
4. Automatically shows all students in class
5. Enter marks for each student
6. Grades auto-save and calculate automatically
7. Letter grades update in real-time

### Workflow 3: Generate Report Card
1. Open ReportCardActivity
2. Select student from spinner
3. Select academic year
4. View all exams and grades in table
5. Overall percentage displayed automatically

## Best Practices

### 1. Date Handling
Always set both `examDate` (Date object) and `examDateMillis` (long):
```java
examination.setExamDate(new Date(selectedDate));
examination.setExamDateMillis(selectedDate);
```

### 2. Saving Grades
Always save via repository or directly to Firestore:
```java
firestore.collection("studentGrades")
    .document(studentId + "_" + examId)
    .set(grade.toMap());
```

### 3. Filtering
Always use Firestore queries for filtering:
```java
firestore.collection("studentGrades")
    .whereEqualTo("examId", selectedExamId)
    .whereEqualTo("classId", selectedClassId)
    .addSnapshotListener(...);
```

### 4. Error Handling
Always handle Firestore errors:
```java
.addOnFailureListener(e -> {
    Toast.makeText(context, "Error: " + e.getMessage(), 
        Toast.LENGTH_SHORT).show();
});
```

## Layout Files

### activity_gradebook.xml
- Spinner for exam selection
- Spinner for class selection
- RecyclerView for student grades
- ProgressBar for loading state

### activity_report_card.xml
- Spinner for student selection
- Spinner for academic year selection
- Student info card (name, year, GPA)
- TableLayout for grade display
- ProgressBar for loading state

### item_gradebook.xml
- Student name (TextView)
- Marks obtained (EditText)
- Calculated grade (TextView)
- Horizontal layout design

## Debugging Tips

### Grades not showing?
1. Check if studentGrades collection exists
2. Verify examId and classId filters are correct
3. Check Firestore permissions
4. Verify student exists in students collection

### Exam not saving?
1. Check if examinations collection exists
2. Verify all required fields are filled
3. Check Firestore permissions
4. Check Firebase initialization

### Adapter not updating?
1. Verify `updateList()` is called
2. Check `notifyDataSetChanged()` is called
3. Verify data is not null
4. Check list size before binding

## Performance Optimization

### 1. Use Pagination
```java
.limit(20) // Load 20 records at a time
.startAfter(lastDocument)
```

### 2. Add Indexes
Create Firestore composite indexes for:
- (examId, classId)
- (studentId, examId)
- (classId, examDate)

### 3. Batch Operations
Use batch writes for bulk updates:
```java
firestore.batch()
    .set(doc1, data1)
    .set(doc2, data2)
    .commit();
```

---

**Status**: ✅ Complete
**Last Updated**: December 10, 2025
