package com.school.management.models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Teacher {
    private String teacherId;
    private String userId;
    private String firstName;
    private String lastName;
    private String employeeId;
    private String email;
    private String phoneNumber;
    private Date dateOfBirth;
    private String gender;
    private String qualification;
    private String experience;
    private List<String> subjects;
    private String designation;
    private Date joiningDate;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String profileImageUrl;
    private double salary;
    private String bankAccountNumber;
    private String bankName;
    private String ifscCode;
    private boolean isActive;
    private boolean isClassTeacher;
    private String assignedClassId;

    public Teacher() {
        // Required for Firebase
    }

    public Teacher(String teacherId, String firstName, String lastName, String employeeId) {
        this.teacherId = teacherId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.employeeId = employeeId;
        this.isActive = true;
    }

    // Getters and Setters
    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public List<String> getSubjects() { return subjects; }
    public void setSubjects(List<String> subjects) { this.subjects = subjects; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public Date getJoiningDate() { return joiningDate; }
    public void setJoiningDate(Date joiningDate) { this.joiningDate = joiningDate; }

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

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean isClassTeacher() { return isClassTeacher; }
    public void setClassTeacher(boolean classTeacher) { isClassTeacher = classTeacher; }

    public String getAssignedClassId() { return assignedClassId; }
    public void setAssignedClassId(String assignedClassId) { this.assignedClassId = assignedClassId; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("teacherId", teacherId);
        map.put("userId", userId);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("employeeId", employeeId);
        map.put("email", email);
        map.put("phoneNumber", phoneNumber);
        map.put("dateOfBirth", dateOfBirth);
        map.put("gender", gender);
        map.put("qualification", qualification);
        map.put("experience", experience);
        map.put("subjects", subjects);
        map.put("designation", designation);
        map.put("joiningDate", joiningDate);
        map.put("address", address);
        map.put("city", city);
        map.put("state", state);
        map.put("zipCode", zipCode);
        map.put("profileImageUrl", profileImageUrl);
        map.put("salary", salary);
        map.put("bankAccountNumber", bankAccountNumber);
        map.put("bankName", bankName);
        map.put("ifscCode", ifscCode);
        map.put("isActive", isActive);
        map.put("isClassTeacher", isClassTeacher);
        map.put("assignedClassId", assignedClassId);
        return map;
    }
}
