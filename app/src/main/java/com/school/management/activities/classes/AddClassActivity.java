package com.school.management.activities.classes;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.R;
import com.school.management.models.SchoolClass;

public class AddClassActivity extends AppCompatActivity {
    private TextInputEditText etClassName, etSection, etGrade, etClassTeacher;
    private TextInputEditText etMaxStudents, etClassroomNumber, etAcademicYear;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        initializeViews();
    }

    private void initializeViews() {
        firestore = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Class");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etClassName = findViewById(R.id.etClassName);
        etSection = findViewById(R.id.etSection);
        etGrade = findViewById(R.id.etGrade);
        etClassTeacher = findViewById(R.id.etClassTeacher);
        etMaxStudents = findViewById(R.id.etMaxStudents);
        etClassroomNumber = findViewById(R.id.etClassroomNumber);
        etAcademicYear = findViewById(R.id.etAcademicYear);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        btnSave.setOnClickListener(v -> validateAndSave());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void validateAndSave() {
        String className = etClassName.getText() != null ? etClassName.getText().toString().trim() : "";
        String section = etSection.getText() != null ? etSection.getText().toString().trim() : "";
        String grade = etGrade.getText() != null ? etGrade.getText().toString().trim() : "";

        if (className.isEmpty() || section.isEmpty() || grade.isEmpty()) {
            Toast.makeText(this, "Please fill in Class Name, Section, and Grade", Toast.LENGTH_SHORT).show();
            return;
        }

        saveClass(className, section, grade);
    }

    private void saveClass(String className, String section, String grade) {
        progressBar.setVisibility(android.view.View.VISIBLE);

        String classId = firestore.collection("classes").document().getId();
        SchoolClass newClass = new SchoolClass(classId, className, section, grade);
        
        newClass.setClassTeacherId(etClassTeacher.getText() != null ? etClassTeacher.getText().toString().trim() : "");
        newClass.setClassroomNumber(etClassroomNumber.getText() != null ? etClassroomNumber.getText().toString().trim() : "");
        newClass.setAcademicYear(etAcademicYear.getText() != null ? etAcademicYear.getText().toString().trim() : "");
        
        try {
            String maxStudentsStr = etMaxStudents.getText() != null ? etMaxStudents.getText().toString().trim() : "50";
            newClass.setMaxStudents(Integer.parseInt(maxStudentsStr));
        } catch (NumberFormatException e) {
            newClass.setMaxStudents(50);
        }
        
        newClass.setActive(true);

        firestore.collection("classes").document(classId).set(newClass.toMap())
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(AddClassActivity.this, "Class created successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(AddClassActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
