package com.school.management.repositories;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.school.management.models.Announcement;

import java.util.List;

public class AnnouncementRepository {
    private final FirebaseFirestore firestore;

    public interface OnCompleteListener {
        void onSuccess();
        void onFailure(String error);
    }

    public interface OnAnnouncementListListener {
        void onSuccess(List<Announcement> announcements);
        void onFailure(String error);
    }

    public AnnouncementRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Save a new announcement to Firestore
     */
    public void saveAnnouncement(Announcement announcement, OnCompleteListener listener) {
        firestore.collection("announcements").document(announcement.getAnnouncementId())
                .set(announcement)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    /**
     * Get all active announcements ordered by posted date (newest first)
     */
    public void getAllAnnouncements(OnAnnouncementListListener listener) {
        firestore.collection("announcements")
                .whereEqualTo("isActive", true)
                .orderBy("postedDate", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (listener != null) listener.onFailure(error.getMessage());
                        return;
                    }

                    if (value != null) {
                        List<Announcement> announcements = value.toObjects(Announcement.class);
                        if (listener != null) listener.onSuccess(announcements);
                    }
                });
    }

    /**
     * Get announcements by category
     */
    public void getAnnouncementsByCategory(String category, OnAnnouncementListListener listener) {
        firestore.collection("announcements")
                .whereEqualTo("category", category)
                .whereEqualTo("isActive", true)
                .orderBy("postedDate", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (listener != null) listener.onFailure(error.getMessage());
                        return;
                    }

                    if (value != null) {
                        List<Announcement> announcements = value.toObjects(Announcement.class);
                        if (listener != null) listener.onSuccess(announcements);
                    }
                });
    }

    /**
     * Get high priority announcements
     */
    public void getHighPriorityAnnouncements(OnAnnouncementListListener listener) {
        firestore.collection("announcements")
                .whereEqualTo("isActive", true)
                .whereIn("priority", java.util.Arrays.asList("HIGH", "URGENT"))
                .orderBy("postedDate", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (listener != null) listener.onFailure(error.getMessage());
                        return;
                    }

                    if (value != null) {
                        List<Announcement> announcements = value.toObjects(Announcement.class);
                        if (listener != null) listener.onSuccess(announcements);
                    }
                });
    }

    /**
     * Update announcement status
     */
    public void updateAnnouncementStatus(String announcementId, boolean isActive, OnCompleteListener listener) {
        firestore.collection("announcements").document(announcementId)
                .update("isActive", isActive)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    /**
     * Delete an announcement
     */
    public void deleteAnnouncement(String announcementId, OnCompleteListener listener) {
        firestore.collection("announcements").document(announcementId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }
}
