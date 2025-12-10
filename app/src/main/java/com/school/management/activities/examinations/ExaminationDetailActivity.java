package com.school.management.activities.examinations;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.R;
import com.school.management.models.Examination;
import com.school.management.utils.DateTimeUtils;

public class ExaminationDetailActivity extends AppCompatActivity {
    private EditText etExamName, etSubject, etTotalMarks, etDescription, etExamDate;
    private EditText etClassName;
    private TextView tvStatus;
    private ProgressBar progressBar;
    private Button btnDelete, btnClose;
    private FirebaseFirestore firestore;
    private String examinationId;
    private Examination currentExamination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examination_detail);

        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Examination Details");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        } catch (Exception e) {
            e.printStackTrace();
        }

        firestore = FirebaseFirestore.getInstance();
        examinationId = getIntent().getStringExtra("examinationId");

        initializeViews();
        setupListeners();
        loadExaminationDetails();
    }

    private void initializeViews() {
        try {
            etExamName = findViewById(R.id.etExamName);
            etSubject = findViewById(R.id.etSubject);
            etTotalMarks = findViewById(R.id.etTotalMarks);
            etDescription = findViewById(R.id.etDescription);
            etExamDate = findViewById(R.id.etExamDate);
            etClassName = findViewById(R.id.etClassName);
            tvStatus = findViewById(R.id.tvStatus);
            progressBar = findViewById(R.id.progressBar);
            btnDelete = findViewById(R.id.btnDelete);
            btnClose = findViewById(R.id.btnClose);

            // Set fields as read-only
            if (etExamName != null) etExamName.setEnabled(false);
            if (etSubject != null) etSubject.setEnabled(false);
            if (etTotalMarks != null) etTotalMarks.setEnabled(false);
            if (etDescription != null) etDescription.setEnabled(false);
            if (etExamDate != null) etExamDate.setEnabled(false);
            if (etClassName != null) etClassName.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        try {
            if (btnDelete != null) {
                btnDelete.setOnClickListener(v -> deleteExamination());
            }
            if (btnClose != null) {
                btnClose.setOnClickListener(v -> onBackPressed());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error setting up listeners: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadExaminationDetails() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        firestore.collection("examinations").document(examinationId)
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    
                    if (error != null) {
                        Toast.makeText(ExaminationDetailActivity.this, "Error loading details", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null && value.exists()) {
                        currentExamination = value.toObject(Examination.class);
                        displayExaminationDetails();
                    }
                });
    }

    private void displayExaminationDetails() {
        etExamName.setText(currentExamination.getExamName());
        etSubject.setText(currentExamination.getSubject());
        etTotalMarks.setText(String.valueOf(currentExamination.getTotalMarks()));
        etDescription.setText(currentExamination.getDescription());
        
        // Handle both Date and long formats
        String dateStr;
        long examTimeMillis;
        if (currentExamination.getExamDate() != null) {
            dateStr = DateTimeUtils.formatDate(currentExamination.getExamDate());
            examTimeMillis = currentExamination.getExamDate().getTime();
        } else if (currentExamination.getExamDateMillis() > 0) {
            dateStr = DateTimeUtils.formatDate(new java.util.Date(currentExamination.getExamDateMillis()));
            examTimeMillis = currentExamination.getExamDateMillis();
        } else {
            dateStr = "N/A";
            examTimeMillis = 0;
        }
        
        etExamDate.setText(dateStr);
        etClassName.setText(currentExamination.getClassName());

        // Update status
        long currentTime = System.currentTimeMillis();
        if (examTimeMillis > 0 && examTimeMillis < currentTime) {
            tvStatus.setText("Completed");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvStatus.setText("Upcoming");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        }
    }

    private void deleteExamination() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Examination")
                .setMessage("Are you sure you want to delete this examination?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    firestore.collection("examinations").document(examinationId).delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ExaminationDetailActivity.this, "Examination deleted", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ExaminationDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("No", null)
                .show();
    }
}
