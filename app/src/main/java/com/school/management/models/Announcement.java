package com.school.management.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Announcement {
    private String announcementId;
    private String title;
    private String description;
    private String category; // ACADEMIC, EVENT, NOTICE, EMERGENCY
    private String priority; // LOW, MEDIUM, HIGH, URGENT
    private Date postedDate;
    private String postedBy;
    private Date expiryDate;
    private boolean isActive;

    public Announcement() {
        // Required for Firebase
    }

    public Announcement(String announcementId, String title, String description, String category) {
        this.announcementId = announcementId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.postedDate = new Date();
        this.priority = "MEDIUM";
        this.isActive = true;
    }

    // Getters and Setters
    public String getAnnouncementId() { return announcementId; }
    public void setAnnouncementId(String announcementId) { this.announcementId = announcementId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Date getPostedDate() { return postedDate; }
    public void setPostedDate(Date postedDate) { this.postedDate = postedDate; }

    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("announcementId", announcementId);
        map.put("title", title);
        map.put("description", description);
        map.put("category", category);
        map.put("priority", priority);
        map.put("postedDate", postedDate);
        map.put("postedBy", postedBy);
        map.put("expiryDate", expiryDate);
        map.put("isActive", isActive);
        return map;
    }
}
