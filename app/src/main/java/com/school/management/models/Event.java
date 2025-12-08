package com.school.management.models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {
    private String eventId;
    private String eventName;
    private String eventType; // SPORTS, CULTURAL, ACADEMIC, HOLIDAY
    private String description;
    private Date startDate;
    private Date endDate;
    private String location;
    private String organizer;
    private List<String> participantIds;
    private int maxParticipants;
    private boolean isRegistrationOpen;
    private String imageUrl;
    private Date createdAt;

    public Event() {
        // Required for Firebase
    }

    public Event(String eventId, String eventName, String eventType) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventType = eventType;
        this.createdAt = new Date();
        this.isRegistrationOpen = true;
    }

    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }

    public List<String> getParticipantIds() { return participantIds; }
    public void setParticipantIds(List<String> participantIds) { this.participantIds = participantIds; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public boolean isRegistrationOpen() { return isRegistrationOpen; }
    public void setRegistrationOpen(boolean registrationOpen) { isRegistrationOpen = registrationOpen; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("eventName", eventName);
        map.put("eventType", eventType);
        map.put("description", description);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("location", location);
        map.put("organizer", organizer);
        map.put("participantIds", participantIds);
        map.put("maxParticipants", maxParticipants);
        map.put("isRegistrationOpen", isRegistrationOpen);
        map.put("imageUrl", imageUrl);
        map.put("createdAt", createdAt);
        return map;
    }
}
