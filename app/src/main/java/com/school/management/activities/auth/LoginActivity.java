package com.school.management.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.school.management.R;
import com.school.management.activities.MainActivity;
import com.school.management.models.User;
import com.school.management.repositories.AuthRepository;
import com.school.management.utils.PreferenceManager;
import com.school.management.utils.Validators;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvRegister;
    private ProgressBar progressBar;
    
    private AuthRepository authRepository;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        initializeRepositories();
        setupClickListeners();
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initializeRepositories() {
        authRepository = new AuthRepository();
        preferenceManager = PreferenceManager.getInstance(this);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        
        tvRegister.setOnClickListener(v -> {
            // Only admins can register new users from settings
            Toast.makeText(this, "Please contact admin to register", Toast.LENGTH_SHORT).show();
        });
        
        // Hidden admin setup - Long press on logo/title 5 times
        View titleView = findViewById(R.id.tvTitle);
        if (titleView != null) {
            final int[] clickCount = {0};
            titleView.setOnClickListener(v -> {
                clickCount[0]++;
                if (clickCount[0] >= 5) {
                    Intent intent = new Intent(LoginActivity.this, 
                            com.school.management.activities.admin.AdminSetupActivity.class);
                    startActivity(intent);
                    clickCount[0] = 0;
                }
            });
        }
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Validators.isValidEmail(email)) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        // Show progress
        showProgress(true);

        // Perform login
        authRepository.login(email, password).observe(this, user -> {
            showProgress(false);
            if (user != null) {
                Toast.makeText(this, "Login successful! Welcome " + user.getFullName(), 
                        Toast.LENGTH_SHORT).show();
                        
                // Save user data to preferences
                preferenceManager.saveUserData(
                        user.getUserId(),
                        user.getRole(),
                        user.getEmail(),
                        user.getFullName()
                );

                // Navigate to main activity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, 
                        "Login failed. Check credentials or account status. See logs for details.", 
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        etEmail.setEnabled(!show);
        etPassword.setEnabled(!show);
    }
}
