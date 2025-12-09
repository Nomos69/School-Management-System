package com.school.management.activities.students;

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
import com.school.management.models.Student;
import com.school.management.models.User;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddStudentActivity extends AppCompatActivity {
    private TextInputEditText etFirstName, etLastName, etAdmissionNumber, etRollNumber, etClass;
    private TextInputEditText etGender, etFatherName, etMotherName, etAddress, etPhoneNumber;
    private Button btnRegister, btnCancel;
    private ProgressBar progressBar;
    private ImageView ivProfile;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        initializeViews();
    }

    private void initializeViews() {
        firestore = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Student");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ivProfile = findViewById(R.id.ivProfile);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etAdmissionNumber = findViewById(R.id.etAdmissionNumber);
        etRollNumber = findViewById(R.id.etRollNumber);
        etClass = findViewById(R.id.etClass);
        etGender = findViewById(R.id.etGender);
        etFatherName = findViewById(R.id.etFatherName);
        etMotherName = findViewById(R.id.etMotherName);
        etAddress = findViewById(R.id.etAddress);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);

        btnRegister = findViewById(R.id.btnRegister);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(v -> validateAndRegister());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void validateAndRegister() {
        String firstName = etFirstName.getText() != null ? etFirstName.getText().toString().trim() : "";
        String lastName = etLastName.getText() != null ? etLastName.getText().toString().trim() : "";
        String admissionNumber = etAdmissionNumber.getText() != null ? etAdmissionNumber.getText().toString().trim() : "";
        String rollNumber = etRollNumber.getText() != null ? etRollNumber.getText().toString().trim() : "";
        String className = etClass.getText() != null ? etClass.getText().toString().trim() : "";

        if (firstName.isEmpty() || lastName.isEmpty() || admissionNumber.isEmpty()) {
            Toast.makeText(this, "Please fill in First, Last, and Admission number", Toast.LENGTH_SHORT).show();
            return;
        }

        registerStudent(firstName, lastName, admissionNumber, rollNumber, className);
    }

    private void registerStudent(String firstName, String lastName, String admissionNumber, String rollNumber, String className) {
        progressBar.setVisibility(android.view.View.VISIBLE);

        String studentId = firestore.collection("students").document().getId();
        Student newStudent = new Student(studentId, firstName, lastName, rollNumber);
        newStudent.setAdmissionNumber(admissionNumber);
        newStudent.setClassId(className);
        newStudent.setGender(etGender.getText() != null ? etGender.getText().toString().trim() : "");
        newStudent.setFatherName(etFatherName.getText() != null ? etFatherName.getText().toString().trim() : "");
        newStudent.setMotherName(etMotherName.getText() != null ? etMotherName.getText().toString().trim() : "");
        newStudent.setAddress(etAddress.getText() != null ? etAddress.getText().toString().trim() : "");
        newStudent.setPhoneNumber(etPhoneNumber.getText() != null ? etPhoneNumber.getText().toString().trim() : "");
        newStudent.setActive(true);
        newStudent.setAdmissionDate(new Date());

        // Save student first
        firestore.collection("students").document(studentId).set(newStudent.toMap())
                .addOnSuccessListener(aVoid -> {
                    // Auto-create user account for student
                    createStudentUser(studentId, firstName, lastName, admissionNumber);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(AddStudentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createStudentUser(String studentId, String firstName, String lastName, String admissionNumber) {
        // Generate email: firstname.lastname@school.com or use admission number
        String email = (firstName.toLowerCase() + "." + lastName.toLowerCase() + "@school.com")
                .replaceAll("\\s+", "");
        String defaultPassword = "School@" + admissionNumber; // Default password
        String fullName = firstName + " " + lastName;

        // Create user in users collection
        String userId = firestore.collection("users").document().getId();
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);
        userData.put("email", email);
        userData.put("fullName", fullName);
        userData.put("role", "STUDENT");
        userData.put("active", true);
        userData.put("createdAt", new Date());
        userData.put("studentId", studentId);
        // Note: In production, hash the password and store securely. For now, storing plaintext for demo.
        userData.put("password", defaultPassword);

        firestore.collection("users").document(userId).set(userData)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(AddStudentActivity.this, 
                            "Student registered successfully!\nLogin: " + email + "\nPassword: " + defaultPassword,
                            Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(AddStudentActivity.this, "Student saved but user creation failed: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
