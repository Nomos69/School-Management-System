package com.school.management.activities.attendance;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.R;
import com.school.management.models.Attendance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceReportActivity extends AppCompatActivity {
    private Spinner spinnerClass, spinnerMonth, spinnerYear;
    private TextView tvPresentCount, tvAbsentCount, tvLateCount, tvExcusedCount, tvAttendancePercentage;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_report);
        
        initializeViews();
        setupToolbar();
    }

    private void initializeViews() {
        firestore = FirebaseFirestore.getInstance();

        spinnerClass = findViewById(R.id.spinnerClass);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);
        
        tvPresentCount = findViewById(R.id.tvPresentCount);
        tvAbsentCount = findViewById(R.id.tvAbsentCount);
        tvLateCount = findViewById(R.id.tvLateCount);
        tvExcusedCount = findViewById(R.id.tvExcusedCount);
        tvAttendancePercentage = findViewById(R.id.tvAttendancePercentage);
        
        progressBar = findViewById(R.id.progressBar);

        spinnerClass.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                loadAttendanceReport();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Attendance Report");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadAttendanceReport() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        String selectedClass = spinnerClass.getSelectedItem().toString();

        firestore.collection("attendance")
                .whereEqualTo("classId", selectedClass)
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    if (error != null) {
                        Toast.makeText(AttendanceReportActivity.this, "Error loading report", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        Map<String, Integer> statusCounts = new HashMap<>();
                        statusCounts.put("PRESENT", 0);
                        statusCounts.put("ABSENT", 0);
                        statusCounts.put("LATE", 0);
                        statusCounts.put("EXCUSED", 0);

                        for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                            Attendance attendance = doc.toObject(Attendance.class);
                            if (attendance != null) {
                                String status = attendance.getStatus();
                                statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
                            }
                        }

                        displayReport(statusCounts, value.size());
                    }
                });
    }

    private void displayReport(Map<String, Integer> statusCounts, int total) {
        int presentCount = statusCounts.getOrDefault("PRESENT", 0);
        int absentCount = statusCounts.getOrDefault("ABSENT", 0);
        int lateCount = statusCounts.getOrDefault("LATE", 0);
        int excusedCount = statusCounts.getOrDefault("EXCUSED", 0);

        tvPresentCount.setText("Present: " + presentCount);
        tvAbsentCount.setText("Absent: " + absentCount);
        tvLateCount.setText("Late: " + lateCount);
        tvExcusedCount.setText("Excused: " + excusedCount);

        double attendancePercentage = total > 0 ? ((double) presentCount / total) * 100 : 0;
        tvAttendancePercentage.setText(String.format("Attendance Rate: %.2f%%", attendancePercentage));
    }
}
