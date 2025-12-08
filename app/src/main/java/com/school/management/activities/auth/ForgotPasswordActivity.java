package com.school.management.activities.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.school.management.R;
import com.school.management.repositories.AuthRepository;
import com.school.management.utils.Validators;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText etEmail;
    private Button btnResetPassword;
    private ProgressBar progressBar;
    
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initializeViews();
        setupToolbar();
        
        authRepository = new AuthRepository();
        
        btnResetPassword.setOnClickListener(v -> attemptPasswordReset());
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.etEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reset Password");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void attemptPasswordReset() {
        String email = etEmail.getText().toString().trim();

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

        showProgress(true);

        authRepository.resetPassword(email).observe(this, success -> {
            showProgress(false);
            if (success) {
                Toast.makeText(this, 
                        "Password reset email sent. Please check your inbox.", 
                        Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, 
                        "Failed to send reset email. Please try again.", 
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnResetPassword.setEnabled(!show);
        etEmail.setEnabled(!show);
    }
}
