package com.school.management.activities.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.R;
import com.school.management.models.User;
import com.school.management.utils.Constants;

import java.util.Date;

public class AdminSetupActivity extends AppCompatActivity {
    private static final String TAG = "AdminSetupActivity";
    private static final String ADMIN_EMAIL = "admin@school.com";
    private static final String ADMIN_PASSWORD = "Admin@123";
    
    private TextView tvStatus;
    private Button btnCheckAdmin, btnCreateAdmin, btnTestLogin;
    private ProgressBar progressBar;
    
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_setup);
        
        initializeViews();
        initializeFirebase();
        setupClickListeners();
    }

    private void initializeViews() {
        tvStatus = findViewById(R.id.tvStatus);
        btnCheckAdmin = findViewById(R.id.btnCheckAdmin);
        btnCreateAdmin = findViewById(R.id.btnCreateAdmin);
        btnTestLogin = findViewById(R.id.btnTestLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void setupClickListeners() {
        btnCheckAdmin.setOnClickListener(v -> checkAdminUser());
        btnCreateAdmin.setOnClickListener(v -> createAdminUser());
        btnTestLogin.setOnClickListener(v -> testAdminLogin());
    }

    private void checkAdminUser() {
        showProgress(true);
        updateStatus("Checking admin user...\n");
        
        // First check Firebase Authentication
        firebaseAuth.fetchSignInMethodsForEmail(ADMIN_EMAIL)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean exists = task.getResult().getSignInMethods() != null 
                                && !task.getResult().getSignInMethods().isEmpty();
                        
                        if (exists) {
                            appendStatus("✅ Admin exists in Firebase Auth\n");
                            
                            // Now check if user document exists in Firestore
                            // Try to login to get UID
                            firebaseAuth.signInWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD)
                                    .addOnSuccessListener(authResult -> {
                                        FirebaseUser user = authResult.getUser();
                                        if (user != null) {
                                            String uid = user.getUid();
                                            appendStatus("Admin UID: " + uid + "\n");
                                            checkFirestoreDocument(uid);
                                        }
                                        firebaseAuth.signOut();
                                    })
                                    .addOnFailureListener(e -> {
                                        appendStatus("❌ Cannot verify Firestore (wrong password?)\n");
                                        showProgress(false);
                                    });
                        } else {
                            appendStatus("❌ Admin NOT found in Firebase Auth\n");
                            appendStatus("Click 'Create Admin' to set up admin account\n");
                            showProgress(false);
                        }
                    } else {
                        appendStatus("❌ Error checking admin: " + task.getException().getMessage() + "\n");
                        showProgress(false);
                    }
                });
    }

    private void checkFirestoreDocument(String uid) {
        firestore.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    showProgress(false);
                    if (documentSnapshot.exists()) {
                        appendStatus("✅ Admin document exists in Firestore\n");
                        
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            appendStatus("Full Name: " + user.getFullName() + "\n");
                            appendStatus("Role: " + user.getRole() + "\n");
                            appendStatus("Active: " + user.isActive() + "\n");
                            
                            if (!user.isActive()) {
                                appendStatus("\n⚠️ WARNING: Admin account is DEACTIVATED!\n");
                                appendStatus("You need to activate it in Firebase Console\n");
                            } else {
                                appendStatus("\n✅ Admin account is ready to use!\n");
                            }
                        }
                    } else {
                        appendStatus("❌ Admin document NOT found in Firestore\n");
                        appendStatus("User exists in Auth but not in Firestore!\n");
                        appendStatus("Click 'Create Admin' to create Firestore document\n");
                    }
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    appendStatus("❌ Error checking Firestore: " + e.getMessage() + "\n");
                });
    }

    private void createAdminUser() {
        showProgress(true);
        updateStatus("Creating admin user...\n");
        
        // First create in Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();
                        appendStatus("✅ Admin created in Firebase Auth\n");
                        appendStatus("UID: " + uid + "\n");
                        
                        // Now create Firestore document
                        createFirestoreDocument(uid);
                    }
                })
                .addOnFailureListener(e -> {
                    // User might already exist, try to create Firestore document anyway
                    if (e.getMessage() != null && e.getMessage().contains("already in use")) {
                        appendStatus("Admin already exists in Auth\n");
                        appendStatus("Attempting to create/update Firestore document...\n");
                        
                        // Login to get UID
                        firebaseAuth.signInWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD)
                                .addOnSuccessListener(authResult -> {
                                    FirebaseUser user = authResult.getUser();
                                    if (user != null) {
                                        createFirestoreDocument(user.getUid());
                                    }
                                    firebaseAuth.signOut();
                                })
                                .addOnFailureListener(loginError -> {
                                    showProgress(false);
                                    appendStatus("❌ Error: " + loginError.getMessage() + "\n");
                                });
                    } else {
                        showProgress(false);
                        appendStatus("❌ Error creating admin: " + e.getMessage() + "\n");
                    }
                });
    }

    private void createFirestoreDocument(String uid) {
        User adminUser = new User(uid, ADMIN_EMAIL, "System Administrator", "ADMIN");
        adminUser.setActive(true);
        adminUser.setCreatedAt(new Date());
        
        firestore.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .set(adminUser.toMap())
                .addOnSuccessListener(aVoid -> {
                    showProgress(false);
                    appendStatus("✅ Admin document created in Firestore\n");
                    appendStatus("\n🎉 Admin setup complete!\n");
                    appendStatus("Email: " + ADMIN_EMAIL + "\n");
                    appendStatus("Password: " + ADMIN_PASSWORD + "\n");
                    appendStatus("\n⚠️ IMPORTANT: Change password after first login!\n");
                    Toast.makeText(this, "Admin created successfully!", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    appendStatus("❌ Error creating Firestore document: " + e.getMessage() + "\n");
                });
    }

    private void testAdminLogin() {
        showProgress(true);
        updateStatus("Testing admin login...\n");
        
        firebaseAuth.signInWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        appendStatus("✅ Firebase Auth successful\n");
                        String uid = firebaseUser.getUid();
                        appendStatus("UID: " + uid + "\n");
                        
                        // Check Firestore
                        firestore.collection(Constants.COLLECTION_USERS)
                                .document(uid)
                                .get()
                                .addOnSuccessListener(doc -> {
                                    showProgress(false);
                                    if (doc.exists()) {
                                        User user = doc.toObject(User.class);
                                        if (user != null && user.isActive()) {
                                            appendStatus("✅ Firestore document found and active\n");
                                            appendStatus("✅ LOGIN WILL WORK!\n");
                                            Toast.makeText(this, "Admin login is working!", Toast.LENGTH_SHORT).show();
                                        } else if (user != null) {
                                            appendStatus("❌ User exists but is DEACTIVATED\n");
                                            Toast.makeText(this, "Admin account is deactivated!", Toast.LENGTH_LONG).show();
                                        } else {
                                            appendStatus("❌ Could not parse user document\n");
                                        }
                                    } else {
                                        appendStatus("❌ User document not found in Firestore\n");
                                        Toast.makeText(this, "Admin document missing in Firestore!", Toast.LENGTH_LONG).show();
                                    }
                                    firebaseAuth.signOut();
                                })
                                .addOnFailureListener(e -> {
                                    showProgress(false);
                                    appendStatus("❌ Firestore error: " + e.getMessage() + "\n");
                                    firebaseAuth.signOut();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    appendStatus("❌ Login failed: " + e.getMessage() + "\n");
                    Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void updateStatus(String text) {
        tvStatus.setText(text);
    }

    private void appendStatus(String text) {
        tvStatus.append(text);
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnCheckAdmin.setEnabled(!show);
        btnCreateAdmin.setEnabled(!show);
        btnTestLogin.setEnabled(!show);
    }
}
