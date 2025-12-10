package com.school.management.activities.classes;

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
import com.school.management.models.SchoolClass;

public class ClassDetailActivity extends AppCompatActivity {
    private TextInputEditText etClassName, etSection, etGrade, etClassTeacher;
    private TextInputEditText etMaxStudents, etClassroomNumber, etAcademicYear;
    private TextView tvStudentCount, tvStatus;
    private Button btnEdit, btnDelete, btnClose;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private SchoolClass currentClass;
    private String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail);

        classId = getIntent().getStringExtra("classId");
        initializeViews();
        setupToolbar();
        loadClassDetails();
    }

    private void initializeViews() {
        firestore = FirebaseFirestore.getInstance();

        etClassName = findViewById(R.id.etClassName);
        etSection = findViewById(R.id.etSection);
        etGrade = findViewById(R.id.etGrade);
        etClassTeacher = findViewById(R.id.etClassTeacher);
        etMaxStudents = findViewById(R.id.etMaxStudents);
        etClassroomNumber = findViewById(R.id.etClassroomNumber);
        etAcademicYear = findViewById(R.id.etAcademicYear);
        tvStudentCount = findViewById(R.id.tvStudentCount);
        tvStatus = findViewById(R.id.tvStatus);

        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnClose = findViewById(R.id.btnClose);
        progressBar = findViewById(R.id.progressBar);

        // Set fields as read-only initially
        etClassName.setEnabled(false);
        etSection.setEnabled(false);
        etGrade.setEnabled(false);
        etClassTeacher.setEnabled(false);
        etMaxStudents.setEnabled(false);
        etClassroomNumber.setEnabled(false);
        etAcademicYear.setEnabled(false);

        btnEdit.setOnClickListener(v -> toggleEditMode());
        btnDelete.setOnClickListener(v -> showDeleteConfirmDialog());
        btnClose.setOnClickListener(v -> finish());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Class Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadClassDetails() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        firestore.collection("classes").document(classId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    if (error != null) {
                        Toast.makeText(ClassDetailActivity.this, "Error loading class", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        currentClass = documentSnapshot.toObject(SchoolClass.class);
                        displayClassDetails();
                    }
                });
    }

    private void displayClassDetails() {
        if (currentClass == null) return;

        etClassName.setText(currentClass.getClassName());
        etSection.setText(currentClass.getSection());
        etGrade.setText(currentClass.getGrade());
        etClassTeacher.setText(currentClass.getClassTeacherId() != null ? currentClass.getClassTeacherId() : "");
        etMaxStudents.setText(String.valueOf(currentClass.getMaxStudents()));
        etClassroomNumber.setText(currentClass.getClassroomNumber() != null ? currentClass.getClassroomNumber() : "");
        etAcademicYear.setText(currentClass.getAcademicYear() != null ? currentClass.getAcademicYear() : "");

        int studentCount = currentClass.getStudentIds() != null ? currentClass.getStudentIds().size() : 0;
        tvStudentCount.setText("Enrolled Students: " + studentCount + "/" + currentClass.getMaxStudents());

        tvStatus.setText(currentClass.isActive() ? "Active" : "Inactive");
        tvStatus.setTextColor(currentClass.isActive() ? 
                getResources().getColor(R.color.success) : 
                getResources().getColor(R.color.error));
    }

    private void toggleEditMode() {
        boolean isEnabled = !etClassName.isEnabled();
        
        etClassName.setEnabled(isEnabled);
        etSection.setEnabled(isEnabled);
        etGrade.setEnabled(isEnabled);
        etClassTeacher.setEnabled(isEnabled);
        etMaxStudents.setEnabled(isEnabled);
        etClassroomNumber.setEnabled(isEnabled);
        etAcademicYear.setEnabled(isEnabled);

        if (isEnabled) {
            btnEdit.setText("Save");
        } else {
            btnEdit.setText("Edit");
            updateClassDetails();
        }
    }

    private void updateClassDetails() {
        progressBar.setVisibility(android.view.View.VISIBLE);

        currentClass.setClassName(etClassName.getText().toString().trim());
        currentClass.setSection(etSection.getText().toString().trim());
        currentClass.setGrade(etGrade.getText().toString().trim());
        currentClass.setClassTeacherId(etClassTeacher.getText().toString().trim());
        currentClass.setClassroomNumber(etClassroomNumber.getText().toString().trim());
        currentClass.setAcademicYear(etAcademicYear.getText().toString().trim());

        try {
            currentClass.setMaxStudents(Integer.parseInt(etMaxStudents.getText().toString().trim()));
        } catch (NumberFormatException e) {
            currentClass.setMaxStudents(50);
        }

        firestore.collection("classes").document(classId).set(currentClass.toMap())
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(ClassDetailActivity.this, "Class updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(ClassDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteConfirmDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete this class? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteClass())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteClass() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        firestore.collection("classes").document(classId).delete()
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(ClassDetailActivity.this, "Class deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(ClassDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
