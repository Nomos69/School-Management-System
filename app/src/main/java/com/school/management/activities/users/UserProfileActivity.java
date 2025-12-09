package com.school.management.activities.users;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.R;
import com.school.management.models.User;
import com.school.management.utils.Constants;
import com.school.management.utils.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {
    private TextView tvUserId, tvFullName, tvEmail, tvRole, tvStatus, tvCreatedAt, tvLastLogin;
    private EditText etPhoneNumber;
    private Button btnEditProfile, btnDeactivate, btnActivate;
    private ProgressBar progressBar;
    private ImageView ivProfile;
    
    private FirebaseFirestore firestore;
    private User currentUser;
    private String userId;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        
        firestore = FirebaseFirestore.getInstance();
        userId = getIntent().getStringExtra("USER_ID");
        
        setupToolbar();
        initializeViews();
        loadUserProfile();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("User Profile");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initializeViews() {
        tvUserId = findViewById(R.id.tvUserId);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);
        tvStatus = findViewById(R.id.tvStatus);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvLastLogin = findViewById(R.id.tvLastLogin);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnDeactivate = findViewById(R.id.btnDeactivate);
        btnActivate = findViewById(R.id.btnActivate);
        progressBar = findViewById(R.id.progressBar);
        ivProfile = findViewById(R.id.ivProfile);
        
        etPhoneNumber.setEnabled(false);
        
        btnEditProfile.setOnClickListener(v -> toggleEditMode());
        btnDeactivate.setOnClickListener(v -> deactivateUser());
        btnActivate.setOnClickListener(v -> activateUser());
    }

    private void loadUserProfile() {
        showProgress(true);
        
        firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentUser = documentSnapshot.toObject(User.class);
                        displayUserInfo();
                        showProgress(false);
                    } else {
                        showProgress(false);
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(this, "Error loading user: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void displayUserInfo() {
        tvUserId.setText("ID: " + currentUser.getUserId());
        tvFullName.setText(currentUser.getFullName());
        tvEmail.setText(currentUser.getEmail());
        tvRole.setText("Role: " + currentUser.getRole());
        etPhoneNumber.setText(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "");
        
        // Status
        if (currentUser.isActive()) {
            tvStatus.setText("Active");
            tvStatus.setTextColor(getResources().getColor(R.color.success));
            btnDeactivate.setVisibility(View.VISIBLE);
            btnActivate.setVisibility(View.GONE);
        } else {
            tvStatus.setText("Inactive");
            tvStatus.setTextColor(getResources().getColor(R.color.error));
            btnDeactivate.setVisibility(View.GONE);
            btnActivate.setVisibility(View.VISIBLE);
        }
        
        // Dates
        if (currentUser.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            tvCreatedAt.setText("Created: " + sdf.format(currentUser.getCreatedAt()));
        }
        
        if (currentUser.getLastLogin() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            tvLastLogin.setText("Last Login: " + sdf.format(currentUser.getLastLogin()));
        } else {
            tvLastLogin.setText("Last Login: Never");
        }
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;
        
        if (isEditMode) {
            etPhoneNumber.setEnabled(true);
            btnEditProfile.setText("Save Changes");
        } else {
            etPhoneNumber.setEnabled(false);
            btnEditProfile.setText("Edit Profile");
            saveChanges();
        }
    }

    private void saveChanges() {
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("phoneNumber", phoneNumber);
        
        firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UserProfileActivity.this, "Profile updated successfully", 
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UserProfileActivity.this, "Error updating profile: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void deactivateUser() {
        new AlertDialog.Builder(this)
                .setTitle("Deactivate User")
                .setMessage("Are you sure you want to deactivate " + currentUser.getFullName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("active", false);
                    
                    firestore.collection(Constants.COLLECTION_USERS)
                            .document(userId)
                            .update(updates)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(UserProfileActivity.this, "User deactivated", 
                                        Toast.LENGTH_SHORT).show();
                                loadUserProfile();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(UserProfileActivity.this, "Error: " + e.getMessage(), 
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void activateUser() {
        new AlertDialog.Builder(this)
                .setTitle("Activate User")
                .setMessage("Are you sure you want to activate " + currentUser.getFullName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("active", true);
                    
                    firestore.collection(Constants.COLLECTION_USERS)
                            .document(userId)
                            .update(updates)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(UserProfileActivity.this, "User activated", 
                                        Toast.LENGTH_SHORT).show();
                                loadUserProfile();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(UserProfileActivity.this, "Error: " + e.getMessage(), 
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            showDeleteConfirmation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete " + currentUser.getFullName() + "? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteUser())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteUser() {
        showProgress(true);
        
        firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    showProgress(false);
                    Toast.makeText(UserProfileActivity.this, "User deleted successfully", 
                            Toast.LENGTH_SHORT).show();
                    onBackPressed();
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(UserProfileActivity.this, "Error deleting user: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }
}
