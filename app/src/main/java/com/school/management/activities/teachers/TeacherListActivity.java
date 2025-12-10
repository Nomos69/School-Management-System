package com.school.management.activities.teachers;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.school.management.R;
import com.school.management.adapters.TeacherAdapter;
import com.school.management.models.Teacher;
import java.util.ArrayList;
import java.util.List;

public class TeacherListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TeacherAdapter adapter;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private FloatingActionButton fabAddTeacher;
    
    private List<Teacher> allTeachers = new ArrayList<>();
    private String currentFilter = "ALL";
    private String currentSort = "firstName";
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_list);
        
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadTeachers();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewTeachers);
        progressBar = findViewById(R.id.progressBar);
        firestore = FirebaseFirestore.getInstance();
        fabAddTeacher = findViewById(R.id.fabAddTeacher);
        
        fabAddTeacher.setOnClickListener(v -> {
            startActivity(new Intent(this, AddTeacherActivity.class));
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Teachers");
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TeacherAdapter(this, allTeachers, teacher -> {
            Intent intent = new Intent(this, TeacherProfileActivity.class);
            intent.putExtra("teacherId", teacher.getTeacherId());
            startActivity(intent);
        });
        adapter.setOnTeacherLongClickListener(teacher -> {
            showDeleteConfirmDialog(teacher);
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadTeachers() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        try {
            // Use simple query without orderBy to avoid index requirements
            firestore.collection("teachers")
                    .addSnapshotListener((value, error) -> {
                        progressBar.setVisibility(android.view.View.GONE);
                        if (error != null) {
                            android.util.Log.e("TeacherListActivity", "Error loading teachers", error);
                            Toast.makeText(TeacherListActivity.this, "Error loading teachers: " + error.getMessage(), 
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        allTeachers.clear();
                        if (value != null) {
                            for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                                Teacher teacher = doc.toObject(Teacher.class);
                                if (teacher != null) {
                                    allTeachers.add(teacher);
                                }
                            }
                        }
                        applyFilter();
                    });
        } catch (Exception e) {
            progressBar.setVisibility(android.view.View.GONE);
            android.util.Log.e("TeacherListActivity", "Exception loading teachers", e);
            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void applyFilter() {
        List<Teacher> filtered = new ArrayList<>();
        for (Teacher teacher : allTeachers) {
            if (passesFilter(teacher) && passesSearch(teacher)) {
                filtered.add(teacher);
            }
        }
        
        // Client-side sorting
        if (currentSort.equals("firstName")) {
            filtered.sort((t1, t2) -> t1.getFirstName().compareToIgnoreCase(t2.getFirstName()));
        } else if (currentSort.equals("employeeId")) {
            filtered.sort((t1, t2) -> {
                String id1 = t1.getEmployeeId() != null ? t1.getEmployeeId() : "";
                String id2 = t2.getEmployeeId() != null ? t2.getEmployeeId() : "";
                return id1.compareToIgnoreCase(id2);
            });
        } else if (currentSort.equals("designation")) {
            filtered.sort((t1, t2) -> {
                String des1 = t1.getDesignation() != null ? t1.getDesignation() : "";
                String des2 = t2.getDesignation() != null ? t2.getDesignation() : "";
                return des1.compareToIgnoreCase(des2);
            });
        } else if (currentSort.equals("isActive")) {
            filtered.sort((t1, t2) -> Boolean.compare(t2.isActive(), t1.isActive()));
        }
        
        adapter.updateList(filtered);
    }

    private boolean passesFilter(Teacher teacher) {
        if (currentFilter.equals("ALL")) return true;
        if (currentFilter.equals("ACTIVE")) return teacher.isActive();
        if (currentFilter.equals("INACTIVE")) return !teacher.isActive();
        if (currentFilter.equals("CLASS_TEACHER")) return teacher.isClassTeacher();
        return true;
    }

    private boolean passesSearch(Teacher teacher) {
        if (searchQuery.isEmpty()) return true;
        String query = searchQuery.toLowerCase();
        return (teacher.getFirstName() != null && teacher.getFirstName().toLowerCase().contains(query)) ||
               (teacher.getLastName() != null && teacher.getLastName().toLowerCase().contains(query)) ||
               (teacher.getEmployeeId() != null && teacher.getEmployeeId().toLowerCase().contains(query)) ||
               (teacher.getEmail() != null && teacher.getEmail().toLowerCase().contains(query));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teachers_list, menu);
        
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
            loadTeachers();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        String[] filters = {"ALL", "ACTIVE", "INACTIVE", "CLASS_TEACHER"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Filter Teachers")
                .setSingleChoiceItems(filters, 0, (dialog, which) -> {
                    currentFilter = filters[which];
                    applyFilter();
                    dialog.dismiss();
                })
                .show();
    }

    private void showSortDialog() {
        String[] sorts = {"First Name", "Employee ID", "Designation", "Active Status"};
        String[] sortValues = {"firstName", "employeeId", "designation", "isActive"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Sort Teachers")
                .setSingleChoiceItems(sorts, 0, (dialog, which) -> {
                    currentSort = sortValues[which];
                    loadTeachers();
                    dialog.dismiss();
                })
                .show();
    }

    private void showDeleteConfirmDialog(Teacher teacher) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Teacher")
                .setMessage("Are you sure you want to delete " + teacher.getFirstName() + " " + teacher.getLastName() + "? This will also delete their user account.")
                .setPositiveButton("Delete", (dialog, which) -> deleteTeacher(teacher))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteTeacher(Teacher teacher) {
        progressBar.setVisibility(android.view.View.VISIBLE);
        
        // Delete teacher record
        firestore.collection("teachers").document(teacher.getTeacherId()).delete()
                .addOnSuccessListener(aVoid -> {
                    // Also delete associated user account
                    deleteTeacherUser(teacher.getTeacherId());
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(this, "Error deleting teacher: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteTeacherUser(String teacherId) {
        // Find and delete user associated with this teacher
        firestore.collection("users")
                .whereEqualTo("teacherId", teacherId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        firestore.collection("users").document(doc.getId()).delete();
                    }
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(TeacherListActivity.this, "Teacher deleted successfully", 
                            Toast.LENGTH_SHORT).show();
                    loadTeachers();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(TeacherListActivity.this, "Error deleting teacher user", 
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTeachers();
    }
}
