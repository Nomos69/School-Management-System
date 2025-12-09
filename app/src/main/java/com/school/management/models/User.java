package com.school.management.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String userId;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String role; // ADMIN, TEACHER, STUDENT, PARENT, STAFF WTF iam sleepy na
    private String profileImageUrl;
    private boolean active; // Changed from isActive to match Firebase boolean naming convention
    private Date createdAt;
    private Date lastLogin;
    private Map<String, Object> permissions;
    private String studentId; // Links user to student record

    public User() {
        // Required for Firebase
    }

    public User(String userId, String email, String fullName, String role) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.active = true;
        this.createdAt = new Date();
        this.permissions = new HashMap<>();
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getLastLogin() { return lastLogin; }
    public void setLastLogin(Date lastLogin) { this.lastLogin = lastLogin; }

    public Map<String, Object> getPermissions() { return permissions; }
    public void setPermissions(Map<String, Object> permissions) { this.permissions = permissions; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("email", email);
        map.put("fullName", fullName);
        map.put("phoneNumber", phoneNumber);
        map.put("role", role);
        map.put("profileImageUrl", profileImageUrl);
        map.put("active", active);
        map.put("createdAt", createdAt);
        map.put("lastLogin", lastLogin);
        map.put("permissions", permissions);
        map.put("studentId", studentId);
        return map;
    }
}
