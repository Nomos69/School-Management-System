package com.school.management.activities.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.R;
import com.school.management.adapters.AttendanceAdapter;
import com.school.management.models.Attendance;

import java.util.ArrayList;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AttendanceAdapter adapter;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private FloatingActionButton fabAddAttendance;
    private List<Attendance> attendanceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadAttendance();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        firestore = FirebaseFirestore.getInstance();
        fabAddAttendance = findViewById(R.id.fabAddAttendance);
        
        fabAddAttendance.setOnClickListener(v -> {
            startActivity(new Intent(this, MarkAttendanceActivity.class));
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Attendance");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttendanceAdapter(this, attendanceList, attendance -> {
            Intent intent = new Intent(this, AttendanceDetailActivity.class);
            intent.putExtra("attendanceId", attendance.getAttendanceId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadAttendance() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        try {
            firestore.collection("attendance")
                    .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .addSnapshotListener((value, error) -> {
                        progressBar.setVisibility(android.view.View.GONE);
                        if (error != null) {
                            android.util.Log.e("AttendanceActivity", "Error loading attendance", error);
                            Toast.makeText(AttendanceActivity.this, "Error loading attendance", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        attendanceList.clear();
                        if (value != null) {
                            for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                                Attendance attendance = doc.toObject(Attendance.class);
                                if (attendance != null) {
                                    attendanceList.add(attendance);
                                }
                            }
                        }
                        adapter.updateList(attendanceList);
                    });
        } catch (Exception e) {
            progressBar.setVisibility(android.view.View.GONE);
            android.util.Log.e("AttendanceActivity", "Exception loading attendance", e);
            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAttendance();
    }
}
