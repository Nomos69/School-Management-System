package com.school.management.activities.examinations;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.R;
import com.school.management.models.Examination;
import com.school.management.utils.DateTimeUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddExaminationActivity extends AppCompatActivity {
    private EditText etExamName, etSubject, etTotalMarks, etDescription, etExamDate;
    private Spinner spinnerClass;
    private Button btnSave, btnCancel;
    private FirebaseFirestore firestore;
    private long selectedDate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_examination);

        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add Examination");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        } catch (Exception e) {
            e.printStackTrace();
        }

        firestore = FirebaseFirestore.getInstance();
        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        try {
            etExamName = findViewById(R.id.etExamName);
            etSubject = findViewById(R.id.etSubject);
            etTotalMarks = findViewById(R.id.etTotalMarks);
            etDescription = findViewById(R.id.etDescription);
            etExamDate = findViewById(R.id.etExamDate);
            spinnerClass = findViewById(R.id.spinnerClass);
            btnSave = findViewById(R.id.btnSave);
            btnCancel = findViewById(R.id.btnCancel);
            
            if (etExamName == null || etSubject == null || etTotalMarks == null) {
                throw new RuntimeException("Some views are not found in the layout");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        try {
            if (etExamDate != null) {
                etExamDate.setOnClickListener(v -> showDatePicker());
            }
            if (btnSave != null) {
                btnSave.setOnClickListener(v -> saveExamination());
            }
            if (btnCancel != null) {
                btnCancel.setOnClickListener(v -> onBackPressed());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error setting up listeners: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            selectedDate = selected.getTimeInMillis();
            etExamDate.setText(DateTimeUtils.formatDate(new Date(selectedDate)));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void saveExamination() {
        String examName = etExamName.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String totalMarksStr = etTotalMarks.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String className = spinnerClass.getSelectedItem().toString();

        if (examName.isEmpty() || subject.isEmpty() || totalMarksStr.isEmpty() || selectedDate == 0) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int totalMarks = Integer.parseInt(totalMarksStr);
            
            String examinationId = UUID.randomUUID().toString();
            Examination examination = new Examination();
            examination.setExaminationId(examinationId);
            examination.setExamId(examinationId);
            examination.setExamName(examName);
            examination.setSubject(subject);
            examination.setSubjectId(subject);
            examination.setTotalMarks(totalMarks);
            examination.setDescription(description);
            examination.setExamDate(new Date(selectedDate));
            examination.setExamDateMillis(selectedDate);
            examination.setClassName(className);
            examination.setCreatedAt(System.currentTimeMillis());
            examination.setUpdatedAt(System.currentTimeMillis());

            firestore.collection("examinations").document(examinationId).set(examination.toMap())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddExaminationActivity.this, "Examination added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddExaminationActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid total marks value", Toast.LENGTH_SHORT).show();
        }
    }
}
