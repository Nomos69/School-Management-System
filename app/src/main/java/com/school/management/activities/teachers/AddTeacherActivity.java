package com.school.management.activities.teachers;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.R;
import com.school.management.models.Teacher;
import com.school.management.models.User;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddTeacherActivity extends AppCompatActivity {
    private TextInputEditText etFirstName, etLastName, etEmployeeId, etEmail, etPhoneNumber;
    private TextInputEditText etQualification, etExperience, etDesignation, etAddress, etCity;
    private TextInputEditText etGender, etSubjects, etSalary;
    private Button btnRegister, btnCancel;
    private ProgressBar progressBar;
    private ImageView ivProfile;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);  

        initializeViews();
    }

    private void initializeViews() {
        firestore = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Teacher");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ivProfile = findViewById(R.id.ivProfile);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmployeeId = findViewById(R.id.etEmployeeId);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etQualification = findViewById(R.id.etQualification);
        etExperience = findViewById(R.id.etExperience);
        etDesignation = findViewById(R.id.etDesignation);
        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etGender = findViewById(R.id.etGender);
        etSubjects = findViewById(R.id.etSubjects);
        etSalary = findViewById(R.id.etSalary);

        btnRegister = findViewById(R.id.btnRegister);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(v -> validateAndRegister());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void validateAndRegister() {
        String firstName = etFirstName.getText() != null ? etFirstName.getText().toString().trim() : "";
        String lastName = etLastName.getText() != null ? etLastName.getText().toString().trim() : "";
        String employeeId = etEmployeeId.getText() != null ? etEmployeeId.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

        if (firstName.isEmpty() || lastName.isEmpty() || employeeId.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in First, Last, Employee ID, and Email", Toast.LENGTH_SHORT).show();
            return;
        }

        registerTeacher(firstName, lastName, employeeId, email);
    }

    private void registerTeacher(String firstName, String lastName, String employeeId, String email) {
        progressBar.setVisibility(android.view.View.VISIBLE);

        String teacherId = firestore.collection("teachers").document().getId();
        Teacher newTeacher = new Teacher(teacherId, firstName, lastName, employeeId);
        newTeacher.setEmail(email);
        newTeacher.setPhoneNumber(etPhoneNumber.getText() != null ? etPhoneNumber.getText().toString().trim() : "");
        newTeacher.setQualification(etQualification.getText() != null ? etQualification.getText().toString().trim() : "");
        newTeacher.setExperience(etExperience.getText() != null ? etExperience.getText().toString().trim() : "");
        newTeacher.setDesignation(etDesignation.getText() != null ? etDesignation.getText().toString().trim() : "");
        newTeacher.setAddress(etAddress.getText() != null ? etAddress.getText().toString().trim() : "");
        newTeacher.setCity(etCity.getText() != null ? etCity.getText().toString().trim() : "");
        newTeacher.setGender(etGender.getText() != null ? etGender.getText().toString().trim() : "");
        
        String subjectsStr = etSubjects.getText() != null ? etSubjects.getText().toString().trim() : "";
        if (!subjectsStr.isEmpty()) {
            java.util.List<String> subjects = java.util.Arrays.asList(subjectsStr.split(","));
            newTeacher.setSubjects(subjects);
        }
        
        try {
            String salaryStr = etSalary.getText() != null ? etSalary.getText().toString().trim() : "0";
            newTeacher.setSalary(Double.parseDouble(salaryStr));
        } catch (NumberFormatException e) {
            newTeacher.setSalary(0);
        }
        
        newTeacher.setActive(true);
        newTeacher.setJoiningDate(new Date());

        // Save teacher first
        firestore.collection("teachers").document(teacherId).set(newTeacher.toMap())
                .addOnSuccessListener(aVoid -> {
                    // Auto-create user account for teacher
                    createTeacherUser(teacherId, firstName, lastName, employeeId, email);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(AddTeacherActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createTeacherUser(String teacherId, String firstName, String lastName, String employeeId, String email) {
        String defaultPassword = "School@" + employeeId; // Default password
        String fullName = firstName + " " + lastName;

        // Create user in users collection
        String userId = firestore.collection("users").document().getId();
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);
        userData.put("email", email);
        userData.put("fullName", fullName);
        userData.put("role", "TEACHER");
        userData.put("active", true);
        userData.put("createdAt", new Date());
        userData.put("teacherId", teacherId);
        userData.put("password", defaultPassword);

        firestore.collection("users").document(userId).set(userData)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(AddTeacherActivity.this,
                            "Teacher registered successfully!\nLogin: " + email + "\nPassword: " + defaultPassword,
                            Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(AddTeacherActivity.this, "Teacher saved but user creation failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
