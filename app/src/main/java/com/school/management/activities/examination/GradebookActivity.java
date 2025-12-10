package com.school.management.activities.examination;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.school.management.R;
import com.school.management.adapters.GradebookAdapter;
import com.school.management.models.StudentGrade;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GradebookActivity extends AppCompatActivity implements GradebookAdapter.OnGradeEditListener {
    private RecyclerView recyclerView;
    private GradebookAdapter adapter;
    private ProgressBar progressBar;
    private Spinner spinnerExam, spinnerClass;
    private FirebaseFirestore firestore;
    private List<StudentGrade> gradeList;
    private String selectedExamId, selectedClassId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gradebook);

        firestore = FirebaseFirestore.getInstance();
        gradeList = new ArrayList<>();

        setupToolbar();
        initializeViews();
        setupRecyclerView();
        setupSpinners();
    }

    private void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Gradebook");
            }
            if (toolbar != null) {
                toolbar.setNavigationOnClickListener(v -> onBackPressed());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        spinnerExam = findViewById(R.id.spinnerExam);
        spinnerClass = findViewById(R.id.spinnerClass);
    }

    private void setupRecyclerView() {
        adapter = new GradebookAdapter(this, gradeList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSpinners() {
        // Load examinations for spinner
        loadExaminationsForSpinner();
        // Load classes for spinner
        loadClassesForSpinner();

        spinnerExam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selected = parent.getItemAtPosition(position).toString();
                    selectedExamId = selected.split(" - ")[0];
                    if (selectedClassId != null) {
                        loadGrades();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selected = parent.getItemAtPosition(position).toString();
                    selectedClassId = selected.split(" - ")[0];
                    if (selectedExamId != null) {
                        loadGrades();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadExaminationsForSpinner() {
        List<String> examList = new ArrayList<>();
        examList.add("Select Examination");

        firestore.collection("examinations")
                .orderBy("examDate", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (int i = examList.size() - 1; i > 0; i--) {
                            examList.remove(i);
                        }
                        value.getDocuments().forEach(doc -> 
                            examList.add(doc.getId() + " - " + doc.getString("examName"))
                        );
                        
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                GradebookActivity.this,
                                android.R.layout.simple_spinner_item,
                                examList
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerExam.setAdapter(adapter);
                    }
                });
    }

    private void loadClassesForSpinner() {
        List<String> classList = new ArrayList<>();
        classList.add("Select Class");

        firestore.collection("classes")
                .orderBy("className", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (int i = classList.size() - 1; i > 0; i--) {
                            classList.remove(i);
                        }
                        value.getDocuments().forEach(doc -> 
                            classList.add(doc.getId() + " - " + doc.getString("className"))
                        );
                        
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                GradebookActivity.this,
                                android.R.layout.simple_spinner_item,
                                classList
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerClass.setAdapter(adapter);
                    }
                });
    }

    private void loadGrades() {
        if (selectedExamId == null || selectedClassId == null) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        
        firestore.collection("studentGrades")
                .whereEqualTo("examId", selectedExamId)
                .whereEqualTo("classId", selectedClassId)
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(View.GONE);

                    if (error != null) {
                        Toast.makeText(GradebookActivity.this, "Error loading grades", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        gradeList.clear();
                        gradeList.addAll(value.toObjects(StudentGrade.class));
                        
                        if (gradeList.isEmpty()) {
                            // Load students and create grade entries if they don't exist
                            loadStudentsAndCreateGrades();
                        } else {
                            adapter.updateList(gradeList);
                        }
                    }
                });
    }

    private void loadStudentsAndCreateGrades() {
        firestore.collection("students")
                .whereEqualTo("classId", selectedClassId)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        gradeList.clear();
                        value.getDocuments().forEach(doc -> {
                            StudentGrade grade = new StudentGrade();
                            grade.setStudentId(doc.getId());
                            grade.setStudentName(doc.getString("firstName") + " " + doc.getString("lastName"));
                            grade.setExamId(selectedExamId);
                            grade.setClassId(selectedClassId);
                            grade.setMarksObtained(0);
                            gradeList.add(grade);
                        });
                        adapter.updateList(gradeList);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (selectedExamId != null && selectedClassId != null) {
            loadGrades();
        }
    }

    @Override
    public void onGradeEdit(StudentGrade grade) {
        // Save the grade to Firestore
        String gradeId = grade.getStudentId() + "_" + selectedExamId;
        firestore.collection("studentGrades").document(gradeId).set(grade)
                .addOnSuccessListener(aVoid -> 
                    Toast.makeText(GradebookActivity.this, "Grade saved", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e -> 
                    Toast.makeText(GradebookActivity.this, "Error saving grade", Toast.LENGTH_SHORT).show()
                );
    }
}
