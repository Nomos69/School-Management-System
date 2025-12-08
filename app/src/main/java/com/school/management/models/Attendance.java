package com.school.management.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Attendance {
    private String attendanceId;
    private String studentId;
    private String classId;
    private Date date;
    private String status; // PRESENT, ABSENT, LATE, EXCUSED
    private String remarks;
    private String markedBy;
    private Date markedAt;

    public Attendance() {
        // Required for Firebase
    }

    public Attendance(String attendanceId, String studentId, String classId, Date date, String status) {
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.classId = classId;
        this.date = date;
        this.status = status;
        this.markedAt = new Date();
    }

    // Getters and Setters
    public String getAttendanceId() { return attendanceId; }
    public void setAttendanceId(String attendanceId) { this.attendanceId = attendanceId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getMarkedBy() { return markedBy; }
    public void setMarkedBy(String markedBy) { this.markedBy = markedBy; }

    public Date getMarkedAt() { return markedAt; }
    public void setMarkedAt(Date markedAt) { this.markedAt = markedAt; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("attendanceId", attendanceId);
        map.put("studentId", studentId);
        map.put("classId", classId);
        map.put("date", date);
        map.put("status", status);
        map.put("remarks", remarks);
        map.put("markedBy", markedBy);
        map.put("markedAt", markedAt);
        return map;
    }
}
