package com.school.management.models;

import java.util.HashMap;
import java.util.Map;

public class StudentGrade {
    private String gradeId;
    private String studentId;
    private String studentName;
    private String examId;
    private String classId;
    private int marksObtained;
    private int totalMarks;
    private String grade; // A, B, C, D, F
    private String remarks;
    private long recordedDate;
    private String recordedBy;

    public StudentGrade() {
        // Required for Firebase
    }

    public StudentGrade(String studentId, String studentName, String examId, String classId) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.examId = examId;
        this.classId = classId;
        this.marksObtained = 0;
        this.recordedDate = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getGradeId() { return gradeId; }
    public void setGradeId(String gradeId) { this.gradeId = gradeId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getExamId() { return examId; }
    public void setExamId(String examId) { this.examId = examId; }

    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }

    public int getMarksObtained() { return marksObtained; }
    public void setMarksObtained(int marksObtained) { this.marksObtained = marksObtained; }

    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int totalMarks) { this.totalMarks = totalMarks; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public long getRecordedDate() { return recordedDate; }
    public void setRecordedDate(long recordedDate) { this.recordedDate = recordedDate; }

    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }

    public String calculateGrade(int marksObtained, int totalMarks) {
        if (totalMarks == 0) return "N/A";
        
        double percentage = (marksObtained * 100.0) / totalMarks;
        
        if (percentage >= 90) return "A";
        else if (percentage >= 80) return "B";
        else if (percentage >= 70) return "C";
        else if (percentage >= 60) return "D";
        else return "F";
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("gradeId", gradeId);
        map.put("studentId", studentId);
        map.put("studentName", studentName);
        map.put("examId", examId);
        map.put("classId", classId);
        map.put("marksObtained", marksObtained);
        map.put("totalMarks", totalMarks);
        map.put("grade", grade);
        map.put("remarks", remarks);
        map.put("recordedDate", recordedDate);
        map.put("recordedBy", recordedBy);
        return map;
    }
}
