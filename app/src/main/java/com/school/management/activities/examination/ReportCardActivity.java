package com.school.management.activities.examination;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.school.management.R;
import com.school.management.models.StudentGrade;
import com.school.management.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class ReportCardActivity extends AppCompatActivity {
    private Spinner spinnerStudent, spinnerAcademicYear;
    private TableLayout tableLayout;
    private ProgressBar progressBar;
    private TextView tvStudentName, tvAcademicYear, tvOverallGPA;
    private FirebaseFirestore firestore;
    private String selectedStudentId, selectedAcademicYear;
    private List<StudentGrade> reportGrades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_card);

        firestore = FirebaseFirestore.getInstance();
        reportGrades = new ArrayList<>();
        
        setupToolbar();
        initializeViews();
        setupSpinners();
    }

    private void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Report Card");
            }
            if (toolbar != null) {
                toolbar.setNavigationOnClickListener(v -> onBackPressed());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeViews() {
        try {
            spinnerStudent = findViewById(R.id.spinnerStudent);
            spinnerAcademicYear = findViewById(R.id.spinnerAcademicYear);
            tableLayout = findViewById(R.id.tableLayout);
            progressBar = findViewById(R.id.progressBar);
            tvStudentName = findViewById(R.id.tvStudentName);
            tvAcademicYear = findViewById(R.id.tvAcademicYear);
            tvOverallGPA = findViewById(R.id.tvOverallGPA);
            
            if (spinnerStudent == null || spinnerAcademicYear == null || tableLayout == null) {
                throw new RuntimeException("Some views are not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSpinners() {
        loadStudentsForSpinner();
        loadAcademicYearsForSpinner();

        spinnerStudent.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selected = parent.getItemAtPosition(position).toString();
                    selectedStudentId = selected.split(" - ")[0];
                    if (selectedAcademicYear != null) {
                        loadReportCard();
                    }
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        spinnerAcademicYear.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedAcademicYear = parent.getItemAtPosition(position).toString();
                    if (selectedStudentId != null) {
                        loadReportCard();
                    }
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void loadStudentsForSpinner() {
        List<String> studentList = new ArrayList<>();
        studentList.add("Select Student");

        firestore.collection("students")
                .orderBy("firstName", Query.Direction.ASCENDING)
                .limit(100)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (int i = studentList.size() - 1; i > 0; i--) {
                            studentList.remove(i);
                        }
                        value.getDocuments().forEach(doc -> {
                            String firstName = doc.getString("firstName");
                            String lastName = doc.getString("lastName");
                            String displayName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
                            studentList.add(doc.getId() + " - " + displayName);
                        });
                        
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                ReportCardActivity.this,
                                android.R.layout.simple_spinner_item,
                                studentList
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerStudent.setAdapter(adapter);
                    }
                });
    }

    private void loadAcademicYearsForSpinner() {
        List<String> yearList = new ArrayList<>();
        yearList.add("Select Academic Year");
        yearList.add("2024-2025");
        yearList.add("2023-2024");
        yearList.add("2022-2023");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                ReportCardActivity.this,
                android.R.layout.simple_spinner_item,
                yearList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAcademicYear.setAdapter(adapter);
    }

    private void loadReportCard() {
        if (selectedStudentId == null || selectedAcademicYear == null) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("studentGrades")
                .whereEqualTo("studentId", selectedStudentId)
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(View.GONE);

                    if (error != null) {
                        Toast.makeText(ReportCardActivity.this, "Error loading report card", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        reportGrades.clear();
                        reportGrades.addAll(value.toObjects(StudentGrade.class));
                        displayReportCard();
                    }
                });
    }

    private void displayReportCard() {
        // Get student information
        firestore.collection("students").document(selectedStudentId)
                .addSnapshotListener((value, error) -> {
                    if (value != null && value.exists()) {
                        String firstName = value.getString("firstName");
                        String lastName = value.getString("lastName");
                        String studentName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
                        tvStudentName.setText(studentName);
                    }
                });

        tvAcademicYear.setText("Academic Year: " + selectedAcademicYear);

        // Clear table
        tableLayout.removeAllViews();

        // Add header row
        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TextView tvHeaderExam = new TextView(this);
        tvHeaderExam.setText("Exam");
        tvHeaderExam.setPadding(10, 10, 10, 10);
        tvHeaderExam.setTypeface(null, android.graphics.Typeface.BOLD);
        headerRow.addView(tvHeaderExam);

        TextView tvHeaderMarks = new TextView(this);
        tvHeaderMarks.setText("Marks");
        tvHeaderMarks.setPadding(10, 10, 10, 10);
        tvHeaderMarks.setTypeface(null, android.graphics.Typeface.BOLD);
        headerRow.addView(tvHeaderMarks);

        TextView tvHeaderGrade = new TextView(this);
        tvHeaderGrade.setText("Grade");
        tvHeaderGrade.setPadding(10, 10, 10, 10);
        tvHeaderGrade.setTypeface(null, android.graphics.Typeface.BOLD);
        headerRow.addView(tvHeaderGrade);

        tableLayout.addView(headerRow);

        // Add grade rows
        double totalGPA = 0;
        for (StudentGrade grade : reportGrades) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            // Get exam name
            firestore.collection("examinations").document(grade.getExamId())
                    .addSnapshotListener((value, error) -> {
                        if (value != null && value.exists()) {
                            TextView tvExamName = new TextView(this);
                            tvExamName.setText(value.getString("examName"));
                            tvExamName.setPadding(10, 10, 10, 10);
                            
                            // Check if row already has views
                            if (row.getChildCount() == 0) {
                                row.addView(tvExamName);
                                
                                TextView tvMarks = new TextView(this);
                                tvMarks.setText(grade.getMarksObtained() + "/" + grade.getTotalMarks());
                                tvMarks.setPadding(10, 10, 10, 10);
                                row.addView(tvMarks);

                                TextView tvGrade = new TextView(this);
                                tvGrade.setText(grade.getGrade() != null ? grade.getGrade() : "N/A");
                                tvGrade.setPadding(10, 10, 10, 10);
                                row.addView(tvGrade);

                                tableLayout.addView(row);
                            }
                        }
                    });
        }

        // Calculate overall GPA (average of all grades)
        if (!reportGrades.isEmpty()) {
            double sumPercentage = 0;
            for (StudentGrade grade : reportGrades) {
                if (grade.getTotalMarks() > 0) {
                    sumPercentage += (grade.getMarksObtained() * 100.0) / grade.getTotalMarks();
                }
            }
            double averagePercentage = sumPercentage / reportGrades.size();
            tvOverallGPA.setText(String.format("Overall Score: %.2f%%", averagePercentage));
        }
    }
}

