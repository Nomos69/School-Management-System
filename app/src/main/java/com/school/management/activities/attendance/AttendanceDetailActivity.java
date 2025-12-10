package com.school.management.activities.attendance;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.R;
import com.school.management.models.Attendance;
import com.school.management.utils.DateTimeUtils;

public class AttendanceDetailActivity extends AppCompatActivity {
    private TextInputEditText etStudentId, etClassId, etDate, etRemarks;
    private Spinner spinnerStatus;
    private TextView tvMarkedBy, tvMarkedAt;
    private Button btnEdit, btnDelete, btnClose;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private Attendance currentAttendance;
    private String attendanceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_detail);

        attendanceId = getIntent().getStringExtra("attendanceId");
        initializeViews();
        setupToolbar();
        loadAttendanceDetails();
    }

    private void initializeViews() {
        firestore = FirebaseFirestore.getInstance();

        etStudentId = findViewById(R.id.etStudentId);
        etClassId = findViewById(R.id.etClassId);
        etDate = findViewById(R.id.etDate);
        etRemarks = findViewById(R.id.etRemarks);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        tvMarkedBy = findViewById(R.id.tvMarkedBy);
        tvMarkedAt = findViewById(R.id.tvMarkedAt);

        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnClose = findViewById(R.id.btnClose);
        progressBar = findViewById(R.id.progressBar);

        // Set fields as read-only initially
        etStudentId.setEnabled(false);
        etClassId.setEnabled(false);
        etDate.setEnabled(false);
        etRemarks.setEnabled(false);
        spinnerStatus.setEnabled(false);

        btnEdit.setOnClickListener(v -> toggleEditMode());
        btnDelete.setOnClickListener(v -> showDeleteConfirmDialog());
        btnClose.setOnClickListener(v -> finish());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Attendance Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadAttendanceDetails() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        firestore.collection("attendance").document(attendanceId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    if (error != null) {
                        Toast.makeText(AttendanceDetailActivity.this, "Error loading attendance", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        currentAttendance = documentSnapshot.toObject(Attendance.class);
                        displayAttendanceDetails();
                    }
                });
    }

    private void displayAttendanceDetails() {
        if (currentAttendance == null) return;

        etStudentId.setText(currentAttendance.getStudentId());
        etClassId.setText(currentAttendance.getClassId());
        etDate.setText(DateTimeUtils.formatDate(currentAttendance.getDate()));
        etRemarks.setText(currentAttendance.getRemarks() != null ? currentAttendance.getRemarks() : "");

        // Set spinner status
        String[] statuses = getResources().getStringArray(R.array.attendance_status_array);
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(currentAttendance.getStatus())) {
                spinnerStatus.setSelection(i);
                break;
            }
        }

        tvMarkedBy.setText("Marked By: " + (currentAttendance.getMarkedBy() != null ? currentAttendance.getMarkedBy() : "N/A"));
        tvMarkedAt.setText("Marked At: " + DateTimeUtils.formatDateTime(currentAttendance.getMarkedAt()));
    }

    private void toggleEditMode() {
        boolean isEnabled = !etRemarks.isEnabled();
        
        etRemarks.setEnabled(isEnabled);
        spinnerStatus.setEnabled(isEnabled);

        if (isEnabled) {
            btnEdit.setText("Save");
        } else {
            btnEdit.setText("Edit");
            updateAttendanceDetails();
        }
    }

    private void updateAttendanceDetails() {
        progressBar.setVisibility(android.view.View.VISIBLE);

        currentAttendance.setStatus(spinnerStatus.getSelectedItem().toString());
        currentAttendance.setRemarks(etRemarks.getText().toString().trim());

        firestore.collection("attendance").document(attendanceId).set(currentAttendance.toMap())
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(AttendanceDetailActivity.this, "Attendance updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(AttendanceDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteConfirmDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Attendance")
                .setMessage("Are you sure you want to delete this attendance record?")
                .setPositiveButton("Delete", (dialog, which) -> deleteAttendance())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAttendance() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        firestore.collection("attendance").document(attendanceId).delete()
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(AttendanceDetailActivity.this, "Attendance deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(AttendanceDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
