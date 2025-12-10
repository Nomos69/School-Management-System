package com.school.management.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Examination {
    private String examId;
    private String examinationId; // Alternative ID field
    private String examName;
    private String examType; // QUIZ, MIDTERM, FINAL, PRACTICAL
    private String classId;
    private String subjectId;
    private String subject; // Alternative subject field
    private String className;
    private String description;
    private Date examDate;
    private long examDateMillis;
    private int totalMarks;
    private int passingMarks;
    private int duration; // in minutes
    private String academicYear;
    private String term;
    private boolean isPublished;
    private long createdAt;
    private long updatedAt;

    public Examination() {
        // Required for Firebase
    }

    public Examination(String examId, String examName, String examType, String classId) {
        this.examId = examId;
        this.examName = examName;
        this.examType = examType;
        this.classId = classId;
        this.isPublished = false;
    }

    // Getters and Setters
    public String getExamId() { return examId; }
    public void setExamId(String examId) { this.examId = examId; }

    public String getExaminationId() { return examinationId != null ? examinationId : examId; }
    public void setExaminationId(String examinationId) { this.examinationId = examinationId; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }

    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }

    public String getSubject() { return subject != null ? subject : subjectId; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getExamDate() { return examDate; }
    public void setExamDate(Date examDate) { this.examDate = examDate; }

    public long getExamDateMillis() { return examDateMillis; }
    public void setExamDateMillis(long examDateMillis) { this.examDateMillis = examDateMillis; }

    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int totalMarks) { this.totalMarks = totalMarks; }

    public int getPassingMarks() { return passingMarks; }
    public void setPassingMarks(int passingMarks) { this.passingMarks = passingMarks; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }

    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("examId", examId);
        map.put("examinationId", examinationId);
        map.put("examName", examName);
        map.put("examType", examType);
        map.put("classId", classId);
        map.put("subjectId", subjectId);
        map.put("subject", subject);
        map.put("className", className);
        map.put("description", description);
        map.put("examDate", examDate);
        map.put("examDateMillis", examDateMillis);
        map.put("totalMarks", totalMarks);
        map.put("passingMarks", passingMarks);
        map.put("duration", duration);
        map.put("academicYear", academicYear);
        map.put("term", term);
        map.put("isPublished", isPublished);
        map.put("createdAt", createdAt);
        map.put("updatedAt", updatedAt);
        return map;
    }
}
