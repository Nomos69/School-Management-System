package com.school.management.activities.events;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.R;
import com.school.management.models.Event;
import com.school.management.utils.DateTimeUtils;

public class EventDetailActivity extends AppCompatActivity {
    private TextInputEditText etEventName, etDescription, etLocation, etOrganizer, etStartDate, etEndDate, etEventType;
    private TextView tvParticipantCount, tvRegistrationStatus;
    private Button btnRegister, btnToggleRegistration, btnDelete, btnClose;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private Event currentEvent;
    private String eventId;
    private boolean isUserRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        eventId = getIntent().getStringExtra("eventId");
        initializeViews();
        setupToolbar();
        loadEventDetails();
    }

    private void initializeViews() {
        firestore = FirebaseFirestore.getInstance();

        etEventName = findViewById(R.id.etEventName);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        etOrganizer = findViewById(R.id.etOrganizer);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        etEventType = findViewById(R.id.etEventType);
        tvParticipantCount = findViewById(R.id.tvParticipantCount);
        tvRegistrationStatus = findViewById(R.id.tvRegistrationStatus);

        btnRegister = findViewById(R.id.btnRegister);
        btnToggleRegistration = findViewById(R.id.btnToggleRegistration);
        btnDelete = findViewById(R.id.btnDelete);
        btnClose = findViewById(R.id.btnClose);
        progressBar = findViewById(R.id.progressBar);

        // Set fields as read-only
        etEventName.setEnabled(false);
        etDescription.setEnabled(false);
        etLocation.setEnabled(false);
        etOrganizer.setEnabled(false);
        etStartDate.setEnabled(false);
        etEndDate.setEnabled(false);
        etEventType.setEnabled(false);

        btnRegister.setOnClickListener(v -> registerForEvent());
        btnToggleRegistration.setOnClickListener(v -> toggleRegistration());
        btnDelete.setOnClickListener(v -> showDeleteConfirmDialog());
        btnClose.setOnClickListener(v -> finish());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Event Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadEventDetails() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        firestore.collection("events").document(eventId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    if (error != null) {
                        Toast.makeText(EventDetailActivity.this, "Error loading event", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        currentEvent = documentSnapshot.toObject(Event.class);
                        displayEventDetails();
                    }
                });
    }

    private void displayEventDetails() {
        if (currentEvent == null) return;

        etEventName.setText(currentEvent.getEventName());
        etDescription.setText(currentEvent.getDescription());
        etLocation.setText(currentEvent.getLocation());
        etOrganizer.setText(currentEvent.getOrganizer());
        etStartDate.setText(DateTimeUtils.formatDate(currentEvent.getStartDate()));
        etEndDate.setText(currentEvent.getEndDate() != null ? DateTimeUtils.formatDate(currentEvent.getEndDate()) : "");
        etEventType.setText(currentEvent.getEventType());

        int participantCount = currentEvent.getParticipantIds() != null ? currentEvent.getParticipantIds().size() : 0;
        tvParticipantCount.setText("Participants: " + participantCount + "/" + currentEvent.getMaxParticipants());

        if (currentEvent.isRegistrationOpen()) {
            tvRegistrationStatus.setText("Registration Open");
            tvRegistrationStatus.setTextColor(getResources().getColor(R.color.success));
            btnRegister.setEnabled(participantCount < currentEvent.getMaxParticipants());
            btnToggleRegistration.setText("Close Registration");
        } else {
            tvRegistrationStatus.setText("Registration Closed");
            tvRegistrationStatus.setTextColor(getResources().getColor(R.color.error));
            btnRegister.setEnabled(false);
            btnToggleRegistration.setText("Open Registration");
        }
    }

    private void registerForEvent() {
        if (currentEvent == null || currentEvent.getParticipantIds() == null) {
            Toast.makeText(this, "Cannot register for this event", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(android.view.View.VISIBLE);
        
        if (!currentEvent.getParticipantIds().contains("user123")) {
            currentEvent.getParticipantIds().add("user123");
        }

        firestore.collection("events").document(eventId).set(currentEvent.toMap())
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(EventDetailActivity.this, "Registered for event successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(EventDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void toggleRegistration() {
        if (currentEvent == null) {
            Toast.makeText(this, "Event not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(android.view.View.VISIBLE);
        currentEvent.setRegistrationOpen(!currentEvent.isRegistrationOpen());

        firestore.collection("events").document(eventId).set(currentEvent.toMap())
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(EventDetailActivity.this, "Registration status updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(EventDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteConfirmDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Delete", (dialog, which) -> deleteEvent())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteEvent() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        firestore.collection("events").document(eventId).delete()
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(EventDetailActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(EventDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
