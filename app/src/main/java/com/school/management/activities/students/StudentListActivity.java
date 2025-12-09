package com.school.management.activities.students;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.school.management.R;
import com.school.management.adapters.StudentAdapter;
import com.school.management.models.Student;
import java.util.ArrayList;
import java.util.List;

public class StudentListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private FloatingActionButton fabAddStudent;
    
    private List<Student> allStudents = new ArrayList<>();
    private String currentFilter = "ALL";
    private String currentSort = "firstName";
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_list);
        
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadStudents();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewStudents);
        progressBar = findViewById(R.id.progressBar);
        firestore = FirebaseFirestore.getInstance();
        fabAddStudent = findViewById(R.id.fabAddStudent);
        
        fabAddStudent.setOnClickListener(v -> {
            startActivity(new Intent(this, AddStudentActivity.class));
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Students");
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(this, allStudents, student -> {
            Intent intent = new Intent(this, StudentProfileActivity.class);
            intent.putExtra("studentId", student.getStudentId());
            startActivity(intent);
        });
        adapter.setOnStudentLongClickListener(student -> {
            showDeleteConfirmDialog(student);
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadStudents() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        firestore.collection("students")
                .orderBy(currentSort, Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    if (error != null) {
                        android.util.Log.e("StudentListActivity", "Error loading students", error);
                        return;
                    }
                    
                    allStudents.clear();
                    if (value != null) {
                        for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                            Student student = doc.toObject(Student.class);
                            if (student != null) {
                                allStudents.add(student);
                            }
                        }
                    }
                    applyFilter();
                });
    }

    private void applyFilter() {
        List<Student> filtered = new ArrayList<>();
        for (Student student : allStudents) {
            if (passesFilter(student) && passesSearch(student)) {
                filtered.add(student);
            }
        }
        adapter.updateList(filtered);
    }

    private boolean passesFilter(Student student) {
        if (currentFilter.equals("ALL")) return true;
        if (currentFilter.equals("ACTIVE")) return student.isActive();
        if (currentFilter.equals("INACTIVE")) return !student.isActive();
        return true;
    }

    private boolean passesSearch(Student student) {
        if (searchQuery.isEmpty()) return true;
        String query = searchQuery.toLowerCase();
        return (student.getFirstName() != null && student.getFirstName().toLowerCase().contains(query)) ||
               (student.getLastName() != null && student.getLastName().toLowerCase().contains(query)) ||
               (student.getAdmissionNumber() != null && student.getAdmissionNumber().toLowerCase().contains(query)) ||
               (student.getRollNumber() != null && student.getRollNumber().toLowerCase().contains(query));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_students_list, menu);
        
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText;
                applyFilter();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            showFilterDialog();
            return true;
        } else if (item.getItemId() == R.id.action_sort) {
            showSortDialog();
            return true;
        } else if (item.getItemId() == R.id.action_refresh) {
            loadStudents();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        String[] filters = {"ALL", "ACTIVE", "INACTIVE"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Filter Students")
                .setSingleChoiceItems(filters, filters.length > 0 ? 0 : -1, (dialog, which) -> {
                    currentFilter = filters[which];
                    applyFilter();
                    dialog.dismiss();
                })
                .show();
    }

    private void showSortDialog() {
        String[] sorts = {"First Name", "Admission Number", "Class", "Active Status"};
        String[] sortValues = {"firstName", "admissionNumber", "classId", "isActive"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Sort Students")
                .setSingleChoiceItems(sorts, 0, (dialog, which) -> {
                    currentSort = sortValues[which];
                    loadStudents();
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudents();
    }

    private void showDeleteConfirmDialog(Student student) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete " + student.getFirstName() + " " + student.getLastName() + "? This will also delete their user account.")
                .setPositiveButton("Delete", (dialog, which) -> deleteStudent(student))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteStudent(Student student) {
        progressBar.setVisibility(android.view.View.VISIBLE);
        
        // Delete student record
        firestore.collection("students").document(student.getStudentId()).delete()
                .addOnSuccessListener(aVoid -> {
                    // Also delete associated user account
                    deleteStudentUser(student.getStudentId());
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    android.widget.Toast.makeText(this, "Error deleting student: " + e.getMessage(), 
                            android.widget.Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteStudentUser(String studentId) {
        // Find and delete user associated with this student
        firestore.collection("users")
                .whereEqualTo("studentId", studentId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        firestore.collection("users").document(doc.getId()).delete();
                    }
                    progressBar.setVisibility(android.view.View.GONE);
                    android.widget.Toast.makeText(StudentListActivity.this, "Student deleted successfully", 
                            android.widget.Toast.LENGTH_SHORT).show();
                    loadStudents();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    android.widget.Toast.makeText(StudentListActivity.this, "Error deleting student user", 
                            android.widget.Toast.LENGTH_SHORT).show();
                });
    }
}
