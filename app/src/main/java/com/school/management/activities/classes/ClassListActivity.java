package com.school.management.activities.classes;

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
import com.school.management.adapters.ClassAdapter;
import com.school.management.models.SchoolClass;

import java.util.ArrayList;
import java.util.List;

public class ClassListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ClassAdapter adapter;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private FloatingActionButton fabAddClass;
    private List<SchoolClass> allClasses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);
        
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadClasses();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        firestore = FirebaseFirestore.getInstance();
        fabAddClass = findViewById(R.id.fabAddClass);
        
        fabAddClass.setOnClickListener(v -> {
            startActivity(new Intent(this, AddClassActivity.class));
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Classes");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClassAdapter(this, allClasses, schoolClass -> {
            Intent intent = new Intent(this, ClassDetailActivity.class);
            intent.putExtra("classId", schoolClass.getClassId());
            startActivity(intent);
        });
        adapter.setOnClassLongClickListener(this::showDeleteConfirmDialog);
        recyclerView.setAdapter(adapter);
    }

    private void loadClasses() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        try {
            firestore.collection("classes")
                    .addSnapshotListener((value, error) -> {
                        progressBar.setVisibility(android.view.View.GONE);
                        if (error != null) {
                            android.util.Log.e("ClassListActivity", "Error loading classes", error);
                            Toast.makeText(ClassListActivity.this, "Error loading classes", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        allClasses.clear();
                        if (value != null) {
                            for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                                SchoolClass schoolClass = doc.toObject(SchoolClass.class);
                                if (schoolClass != null) {
                                    allClasses.add(schoolClass);
                                }
                            }
                        }
                        
                        // Sort by class name
                        allClasses.sort((c1, c2) -> c1.getClassName().compareToIgnoreCase(c2.getClassName()));
                        adapter.updateList(allClasses);
                    });
        } catch (Exception e) {
            progressBar.setVisibility(android.view.View.GONE);
            android.util.Log.e("ClassListActivity", "Exception loading classes", e);
            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmDialog(SchoolClass schoolClass) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete " + schoolClass.getFullClassName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteClass(schoolClass))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteClass(SchoolClass schoolClass) {
        progressBar.setVisibility(android.view.View.VISIBLE);
        firestore.collection("classes").document(schoolClass.getClassId()).delete()
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(this, "Class deleted successfully", Toast.LENGTH_SHORT).show();
                    loadClasses();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClasses();
    }
}
