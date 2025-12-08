package com.school.management.utils;

public class Constants {
    // User Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_TEACHER = "TEACHER";
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_PARENT = "PARENT";
    public static final String ROLE_STAFF = "STAFF";

    // Attendance Status
    public static final String ATTENDANCE_PRESENT = "PRESENT";
    public static final String ATTENDANCE_ABSENT = "ABSENT";
    public static final String ATTENDANCE_LATE = "LATE";
    public static final String ATTENDANCE_EXCUSED = "EXCUSED";

    // Exam Types
    public static final String EXAM_QUIZ = "QUIZ";
    public static final String EXAM_MIDTERM = "MIDTERM";
    public static final String EXAM_FINAL = "FINAL";
    public static final String EXAM_PRACTICAL = "PRACTICAL";

    // Fee Types
    public static final String FEE_TUITION = "TUITION";
    public static final String FEE_BUS = "BUS";
    public static final String FEE_LIBRARY = "LIBRARY";
    public static final String FEE_EXAM = "EXAM";
    public static final String FEE_MISC = "MISC";

    // Payment Status
    public static final String PAYMENT_PAID = "PAID";
    public static final String PAYMENT_PARTIAL = "PARTIAL";
    public static final String PAYMENT_PENDING = "PENDING";
    public static final String PAYMENT_OVERDUE = "OVERDUE";

    // Payment Methods
    public static final String PAYMENT_CASH = "CASH";
    public static final String PAYMENT_CARD = "CARD";
    public static final String PAYMENT_ONLINE = "ONLINE";
    public static final String PAYMENT_CHEQUE = "CHEQUE";

    // Book Issue Status
    public static final String BOOK_ISSUED = "ISSUED";
    public static final String BOOK_RETURNED = "RETURNED";
    public static final String BOOK_OVERDUE = "OVERDUE";

    // Event Types
    public static final String EVENT_SPORTS = "SPORTS";
    public static final String EVENT_CULTURAL = "CULTURAL";
    public static final String EVENT_ACADEMIC = "ACADEMIC";
    public static final String EVENT_HOLIDAY = "HOLIDAY";

    // Message Types
    public static final String MESSAGE_DIRECT = "DIRECT";
    public static final String MESSAGE_ANNOUNCEMENT = "ANNOUNCEMENT";
    public static final String MESSAGE_ALERT = "ALERT";

    // Message Priority
    public static final String PRIORITY_LOW = "LOW";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_URGENT = "URGENT";

    // Firebase Collections
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_STUDENTS = "students";
    public static final String COLLECTION_TEACHERS = "teachers";
    public static final String COLLECTION_CLASSES = "classes";
    public static final String COLLECTION_SUBJECTS = "subjects";
    public static final String COLLECTION_ATTENDANCE = "attendance";
    public static final String COLLECTION_EXAMINATIONS = "examinations";
    public static final String COLLECTION_GRADES = "grades";
    public static final String COLLECTION_FEES = "fees";
    public static final String COLLECTION_LIBRARY_BOOKS = "library_books";
    public static final String COLLECTION_BOOK_ISSUES = "book_issues";
    public static final String COLLECTION_EVENTS = "events";
    public static final String COLLECTION_MESSAGES = "messages";
    public static final String COLLECTION_ANNOUNCEMENTS = "announcements";
    public static final String COLLECTION_TIMETABLE = "timetable";
    public static final String COLLECTION_LEAVE_APPLICATIONS = "leave_applications";
    public static final String COLLECTION_AUDIT_LOGS = "audit_logs";

    // Shared Preferences Keys
    public static final String PREF_NAME = "SchoolManagementPrefs";
    public static final String PREF_USER_ID = "userId";
    public static final String PREF_USER_ROLE = "userRole";
    public static final String PREF_USER_EMAIL = "userEmail";
    public static final String PREF_USER_NAME = "userName";
    public static final String PREF_IS_LOGGED_IN = "isLoggedIn";

    // Intent Keys
    public static final String EXTRA_USER_ID = "userId";
    public static final String EXTRA_STUDENT_ID = "studentId";
    public static final String EXTRA_TEACHER_ID = "teacherId";
    public static final String EXTRA_CLASS_ID = "classId";
    public static final String EXTRA_EXAM_ID = "examId";
    public static final String EXTRA_BOOK_ID = "bookId";
    public static final String EXTRA_EVENT_ID = "eventId";
    public static final String EXTRA_MESSAGE_ID = "messageId";

    // Date Formats
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String TIME_FORMAT = "hh:mm a";
    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy hh:mm a";

    // Request Codes
    public static final int REQUEST_IMAGE_PICK = 1001;
    public static final int REQUEST_IMAGE_CAPTURE = 1002;
    public static final int REQUEST_PERMISSION_CAMERA = 1003;
    public static final int REQUEST_PERMISSION_STORAGE = 1004;
    public static final int REQUEST_BARCODE_SCAN = 1005;

    // Notification Channels
    public static final String CHANNEL_ID_GENERAL = "general_notifications";
    public static final String CHANNEL_ID_ATTENDANCE = "attendance_notifications";
    public static final String CHANNEL_ID_FEES = "fee_notifications";
    public static final String CHANNEL_ID_EXAMS = "exam_notifications";
    public static final String CHANNEL_ID_MESSAGES = "message_notifications";
}
