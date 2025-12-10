package com.school.management.activities.attendance;

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
import com.school.management.models.Attendance;
import com.school.management.utils.DateTimeUtils;

import java.util.Calendar;
import java.util.Date;

public class MarkAttendanceActivity extends AppCompatActivity {
    private TextInputEditText etStudentId, etClassId, etRemarks, etDate;
    private Spinner spinnerStatus;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        initializeViews();
    }

    private void initializeViews() {
        firestore = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mark Attendance");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etStudentId = findViewById(R.id.etStudentId);
        etClassId = findViewById(R.id.etClassId);
        etDate = findViewById(R.id.etDate);
        etRemarks = findViewById(R.id.etRemarks);
        spinnerStatus = findViewById(R.id.spinnerStatus);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        selectedDate = new Date();
        etDate.setText(DateTimeUtils.formatDate(selectedDate));
        
        etDate.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> validateAndSave());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (selectedDate != null) {
            calendar.setTime(selectedDate);
        }
        
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.set(year, month, dayOfMonth);
            selectedDate = selectedCal.getTime();
            etDate.setText(DateTimeUtils.formatDate(selectedDate));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void validateAndSave() {
        String studentId = etStudentId.getText() != null ? etStudentId.getText().toString().trim() : "";
        String classId = etClassId.getText() != null ? etClassId.getText().toString().trim() : "";
        String status = spinnerStatus.getSelectedItem().toString();

        if (studentId.isEmpty() || classId.isEmpty()) {
            Toast.makeText(this, "Please fill in Student ID and Class ID", Toast.LENGTH_SHORT).show();
            return;
        }

        saveAttendance(studentId, classId, status);
    }

    private void saveAttendance(String studentId, String classId, String status) {
        progressBar.setVisibility(android.view.View.VISIBLE);

        String attendanceId = firestore.collection("attendance").document().getId();
        Attendance attendance = new Attendance(attendanceId, studentId, classId, selectedDate, status);
        
        attendance.setRemarks(etRemarks.getText() != null ? etRemarks.getText().toString().trim() : "");
        attendance.setMarkedAt(new Date());

        firestore.collection("attendance").document(attendanceId).set(attendance.toMap())
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(MarkAttendanceActivity.this, "Attendance marked successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(MarkAttendanceActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
