package com.school.management.activities.examination;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.school.management.R;
import com.school.management.adapters.ExaminationAdapter;
import com.school.management.activities.examinations.AddExaminationActivity;
import com.school.management.activities.examinations.ExaminationDetailActivity;
import com.school.management.models.Examination;

import java.util.ArrayList;
import java.util.List;

public class ExamListActivity extends AppCompatActivity implements ExaminationAdapter.OnExaminationClickListener {
    private RecyclerView recyclerView;
    private ExaminationAdapter adapter;
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private FirebaseFirestore firestore;
    private List<Examination> examinationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list);

        firestore = FirebaseFirestore.getInstance();
        examinationList = new ArrayList<>();

        setupToolbar();
        initializeViews();
        setupRecyclerView();
        setupFAB();
        loadExaminations();
    }

    private void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Examinations");
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
            recyclerView = findViewById(R.id.recyclerView);
            progressBar = findViewById(R.id.progressBar);
            fab = findViewById(R.id.fab);
            
            if (recyclerView == null || fab == null) {
                throw new RuntimeException("Some views are not found in the layout");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        adapter = new ExaminationAdapter(this, examinationList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupFAB() {
        try {
            if (fab != null) {
                fab.setOnClickListener(v -> {
                    Intent intent = new Intent(ExamListActivity.this, AddExaminationActivity.class);
                    startActivity(intent);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error setting up FAB: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadExaminations() {
        progressBar.setVisibility(View.VISIBLE);
        firestore.collection("examinations")
                .orderBy("examDate", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(View.GONE);

                    if (error != null) {
                        Toast.makeText(ExamListActivity.this, "Error loading examinations", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        examinationList.clear();
                        examinationList.addAll(value.toObjects(Examination.class));
                        adapter.updateList(examinationList);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExaminations();
    }

    @Override
    public void onExaminationClick(Examination examination) {
        Intent intent = new Intent(ExamListActivity.this, ExaminationDetailActivity.class);
        intent.putExtra("examinationId", examination.getExaminationId());
        startActivity(intent);
    }
}
