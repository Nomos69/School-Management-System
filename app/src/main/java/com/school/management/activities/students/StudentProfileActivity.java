package com.school.management.activities.students;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.R;

public class StudentProfileActivity extends AppCompatActivity {
    private String studentId;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        
        firestore = FirebaseFirestore.getInstance();
        studentId = getIntent().getStringExtra("studentId");
        
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Student Profile");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_student_profile, menu);
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
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete this student? This will also delete their user account.")
                .setPositiveButton("Delete", (dialog, which) -> deleteStudent())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteStudent() {
        // Delete student record
        firestore.collection("students").document(studentId).delete()
                .addOnSuccessListener(aVoid -> {
                    // Delete associated user account
                    deleteStudentUser();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error deleting student: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteStudentUser() {
        // Find and delete user associated with this student
        firestore.collection("users")
                .whereEqualTo("studentId", studentId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        firestore.collection("users").document(doc.getId()).delete();
                    }
                    Toast.makeText(StudentProfileActivity.this, "Student deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(StudentProfileActivity.this, "Error deleting student user", Toast.LENGTH_SHORT).show();
                });
    }
}
