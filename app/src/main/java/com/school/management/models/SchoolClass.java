package com.school.management.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchoolClass {
    private String classId;
    private String className;
    private String section;
    private String grade;
    private String classTeacherId;
    private List<String> subjectIds;
    private List<String> studentIds;
    private int maxStudents;
    private String classroomNumber;
    private String academicYear;
    private boolean isActive;

    public SchoolClass() {
        // Required for Firebase
    }

    public SchoolClass(String classId, String className, String section, String grade) {
        this.classId = classId;
        this.className = className;
        this.section = section;
        this.grade = grade;
        this.isActive = true;
    }

    // Getters and Setters
    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getClassTeacherId() { return classTeacherId; }
    public void setClassTeacherId(String classTeacherId) { this.classTeacherId = classTeacherId; }

    public List<String> getSubjectIds() { return subjectIds; }
    public void setSubjectIds(List<String> subjectIds) { this.subjectIds = subjectIds; }

    public List<String> getStudentIds() { return studentIds; }
    public void setStudentIds(List<String> studentIds) { this.studentIds = studentIds; }

    public int getMaxStudents() { return maxStudents; }
    public void setMaxStudents(int maxStudents) { this.maxStudents = maxStudents; }

    public String getClassroomNumber() { return classroomNumber; }
    public void setClassroomNumber(String classroomNumber) { this.classroomNumber = classroomNumber; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getFullClassName() {
        return className + " - " + section;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("classId", classId);
        map.put("className", className);
        map.put("section", section);
        map.put("grade", grade);
        map.put("classTeacherId", classTeacherId);
        map.put("subjectIds", subjectIds);
        map.put("studentIds", studentIds);
        map.put("maxStudents", maxStudents);
        map.put("classroomNumber", classroomNumber);
        map.put("academicYear", academicYear);
        map.put("isActive", isActive);
        return map;
    }
}
