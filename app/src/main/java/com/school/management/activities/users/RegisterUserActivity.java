package com.school.management.activities.users;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.R;
import com.school.management.models.User;
import com.school.management.utils.Constants;
import com.school.management.utils.Validators;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterUserActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etFullName, etPhoneNumber;
    private Spinner spinnerRole;
    private Button btnRegister, btnCancel;
    private ProgressBar progressBar;
    private ImageView ivProfile;
    
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        
        firestore = FirebaseFirestore.getInstance();
        
        setupToolbar();
        initializeViews();
        setupClickListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Register New User");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etFullName = findViewById(R.id.etFullName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnRegister = findViewById(R.id.btnRegister);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
        ivProfile = findViewById(R.id.ivProfile);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> validateAndRegister());
        btnCancel.setOnClickListener(v -> onBackPressed());
        
        ivProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Profile photo upload coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void validateAndRegister() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();

        // Validation
        if (!Validators.isValidEmail(email)) {
            etEmail.setError("Enter a valid email");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            return;
        }

        if (role.equals("Select Role")) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        registerUser(email, password, fullName, phoneNumber, role);
    }

    private void registerUser(String email, String password, String fullName, String phoneNumber, String role) {
        showProgress(true);

        // Call your backend or Firebase Auth to create user
        // For now, we'll create a document in Firestore
        String userId = firestore.collection(Constants.COLLECTION_USERS).document().getId();
        
        User newUser = new User(userId, email, fullName, role);
        newUser.setPhoneNumber(phoneNumber);
        newUser.setActive(true);
        newUser.setCreatedAt(new Date());

        firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .set(newUser.toMap())
                .addOnSuccessListener(aVoid -> {
                    showProgress(false);
                    Toast.makeText(RegisterUserActivity.this, 
                            "User registered successfully! Password: " + password, 
                            Toast.LENGTH_LONG).show();
                    onBackPressed();
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(RegisterUserActivity.this, 
                            "Error registering user: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
        btnCancel.setEnabled(!show);
    }
}
