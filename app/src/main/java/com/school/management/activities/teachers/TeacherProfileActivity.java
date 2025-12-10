package com.school.management.activities.teachers;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.R;

public class TeacherProfileActivity extends AppCompatActivity {
    private String teacherId;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);
        
        firestore = FirebaseFirestore.getInstance();
        teacherId = getIntent().getStringExtra("teacherId");
        
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Teacher Profile");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teacher_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            showDeleteConfirmDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Teacher")
                .setMessage("Are you sure you want to delete this teacher? This will also delete their user account.")
                .setPositiveButton("Delete", (dialog, which) -> deleteTeacher())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteTeacher() {
        // Delete teacher record
        firestore.collection("teachers").document(teacherId).delete()
                .addOnSuccessListener(aVoid -> {
                    // Delete associated user account
                    deleteTeacherUser();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error deleting teacher: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteTeacherUser() {
        // Find and delete user associated with this teacher
        firestore.collection("users")
                .whereEqualTo("teacherId", teacherId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        firestore.collection("users").document(doc.getId()).delete();
                    }
                    Toast.makeText(TeacherProfileActivity.this, "Teacher deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TeacherProfileActivity.this, "Error deleting teacher user", Toast.LENGTH_SHORT).show();
                });
    }
}
