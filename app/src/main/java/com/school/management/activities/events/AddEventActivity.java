package com.school.management.activities.events;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.R;
import com.school.management.models.Event;
import com.school.management.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddEventActivity extends AppCompatActivity {
    private TextInputEditText etEventName, etDescription, etLocation, etOrganizer, etStartDate, etEndDate, etMaxParticipants;
    private Spinner spinnerEventType;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private Date selectedStartDate, selectedEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        initializeViews();
    }

    private void initializeViews() {
        firestore = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Event");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etEventName = findViewById(R.id.etEventName);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        etOrganizer = findViewById(R.id.etOrganizer);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        etMaxParticipants = findViewById(R.id.etMaxParticipants);
        spinnerEventType = findViewById(R.id.spinnerEventType);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        etStartDate.setFocusable(false);
        etStartDate.setClickable(true);
        etStartDate.setOnClickListener(v -> showStartDatePicker());

        etEndDate.setFocusable(false);
        etEndDate.setClickable(true);
        etEndDate.setOnClickListener(v -> showEndDatePicker());

        btnSave.setOnClickListener(v -> saveEvent());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showStartDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            selectedStartDate = calendar.getTime();
            etStartDate.setText(DateTimeUtils.formatDate(selectedStartDate));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showEndDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            selectedEndDate = calendar.getTime();
            etEndDate.setText(DateTimeUtils.formatDate(selectedEndDate));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveEvent() {
        String eventName = etEventName.getText() != null ? etEventName.getText().toString().trim() : "";
        String eventType = spinnerEventType.getSelectedItem().toString();

        if (eventName.isEmpty()) {
            Toast.makeText(this, "Please enter event name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedStartDate == null) {
            Toast.makeText(this, "Please select start date", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(android.view.View.VISIBLE);

        String eventId = UUID.randomUUID().toString();
        Event event = new Event(eventId, eventName, eventType);
        
        event.setDescription(etDescription.getText() != null ? etDescription.getText().toString().trim() : "");
        event.setLocation(etLocation.getText() != null ? etLocation.getText().toString().trim() : "");
        event.setOrganizer(etOrganizer.getText() != null ? etOrganizer.getText().toString().trim() : "");
        event.setStartDate(selectedStartDate);
        event.setEndDate(selectedEndDate);
        event.setParticipantIds(new ArrayList<>());

        try {
            int maxParticipants = Integer.parseInt(etMaxParticipants.getText() != null ? etMaxParticipants.getText().toString().trim() : "100");
            event.setMaxParticipants(maxParticipants);
        } catch (NumberFormatException e) {
            event.setMaxParticipants(100);
        }

        firestore.collection("events").document(eventId).set(event.toMap())
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(AddEventActivity.this, "Event created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(AddEventActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
