package com.school.management.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Student {
    private String studentId;
    private String userId;
    private String firstName;
    private String lastName;
    private String rollNumber;
    private String admissionNumber;
    private Date dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String classId;
    private String sectionId;
    private Date admissionDate;
    private String previousSchool;
    private String previousEducation;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String profileImageUrl;
    private boolean isActive;
    
    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;
    
    // Parent Information
    private String fatherName;
    private String fatherPhone;
    private String fatherOccupation;
    private String motherName;
    private String motherPhone;
    private String motherOccupation;
    private String guardianId;

    public Student() {
        // Required for Firebase
    }

    public Student(String studentId, String firstName, String lastName, String rollNumber) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.rollNumber = rollNumber;
        this.isActive = true;
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getAdmissionNumber() { return admissionNumber; }
    public void setAdmissionNumber(String admissionNumber) { this.admissionNumber = admissionNumber; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }

    public String getSectionId() { return sectionId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }

    public Date getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(Date admissionDate) { this.admissionDate = admissionDate; }

    public String getPreviousSchool() { return previousSchool; }
    public void setPreviousSchool(String previousSchool) { this.previousSchool = previousSchool; }

    public String getPreviousEducation() { return previousEducation; }
    public void setPreviousEducation(String previousEducation) { this.previousEducation = previousEducation; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }

    public String getEmergencyContactRelation() { return emergencyContactRelation; }
    public void setEmergencyContactRelation(String emergencyContactRelation) { this.emergencyContactRelation = emergencyContactRelation; }

    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }

    public String getFatherPhone() { return fatherPhone; }
    public void setFatherPhone(String fatherPhone) { this.fatherPhone = fatherPhone; }

    public String getFatherOccupation() { return fatherOccupation; }
    public void setFatherOccupation(String fatherOccupation) { this.fatherOccupation = fatherOccupation; }

    public String getMotherName() { return motherName; }
    public void setMotherName(String motherName) { this.motherName = motherName; }

    public String getMotherPhone() { return motherPhone; }
    public void setMotherPhone(String motherPhone) { this.motherPhone = motherPhone; }

    public String getMotherOccupation() { return motherOccupation; }
    public void setMotherOccupation(String motherOccupation) { this.motherOccupation = motherOccupation; }

    public String getGuardianId() { return guardianId; }
    public void setGuardianId(String guardianId) { this.guardianId = guardianId; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("studentId", studentId);
        map.put("userId", userId);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("rollNumber", rollNumber);
        map.put("admissionNumber", admissionNumber);
        map.put("dateOfBirth", dateOfBirth);
        map.put("gender", gender);
        map.put("bloodGroup", bloodGroup);
        map.put("classId", classId);
        map.put("sectionId", sectionId);
        map.put("admissionDate", admissionDate);
        map.put("previousSchool", previousSchool);
        map.put("previousEducation", previousEducation);
        map.put("address", address);
        map.put("city", city);
        map.put("state", state);
        map.put("zipCode", zipCode);
        map.put("profileImageUrl", profileImageUrl);
        map.put("isActive", isActive);
        map.put("emergencyContactName", emergencyContactName);
        map.put("emergencyContactPhone", emergencyContactPhone);
        map.put("emergencyContactRelation", emergencyContactRelation);
        map.put("fatherName", fatherName);
        map.put("fatherPhone", fatherPhone);
        map.put("fatherOccupation", fatherOccupation);
        map.put("motherName", motherName);
        map.put("motherPhone", motherPhone);
        map.put("motherOccupation", motherOccupation);
        map.put("guardianId", guardianId);
        return map;
    }
}
