package com.school.management.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.models.User;
import com.school.management.utils.Constants;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    public AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<User> login(String email, String password) {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        
        Log.d(TAG, "Attempting login for email: " + email);
        
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            Log.d(TAG, "Firebase Auth successful. UID: " + firebaseUser.getUid());
                            getUserDetails(firebaseUser.getUid(), userLiveData);
                            // Don't update lastLogin yet - wait until we confirm user doc exists
                        } else {
                            Log.e(TAG, "Firebase Auth successful but firebaseUser is null");
                            userLiveData.setValue(null);
                        }
                    } else {
                        Log.e(TAG, "Firebase Auth failed", task.getException());
                        if (task.getException() != null) {
                            Log.e(TAG, "Error message: " + task.getException().getMessage());
                        }
                        userLiveData.setValue(null);
                    }
                });
        
        return userLiveData;
    }

    public MutableLiveData<User> register(User user, String password) {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            user.setUserId(firebaseUser.getUid());
                            user.setCreatedAt(new Date());
                            saveUserToFirestore(user, userLiveData);
                            logAuditEvent(user.getUserId(), "USER_REGISTERED", "User registered successfully");
                        }
                    } else {
                        Log.e(TAG, "Registration failed", task.getException());
                        userLiveData.setValue(null);
                    }
                });
        
        return userLiveData;
    }

    private void saveUserToFirestore(User user, MutableLiveData<User> userLiveData) {
        firestore.collection(Constants.COLLECTION_USERS)
                .document(user.getUserId())
                .set(user.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User saved to Firestore");
                    userLiveData.setValue(user);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving user", e);
                    userLiveData.setValue(null);
                });
    }

    private void getUserDetails(String userId, MutableLiveData<User> userLiveData) {
        Log.d(TAG, "Getting user details for userId: " + userId);
        firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "User document found. Raw data: " + documentSnapshot.getData());
                        
                        try {
                            User user = documentSnapshot.toObject(User.class);
                            
                            if (user != null) {
                                Log.d(TAG, "User deserialized successfully");
                                Log.d(TAG, "User ID: " + user.getUserId());
                                Log.d(TAG, "Email: " + user.getEmail());
                                Log.d(TAG, "Full Name: " + user.getFullName());
                                Log.d(TAG, "Role: " + user.getRole());
                                Log.d(TAG, "isActive: " + user.isActive());
                                
                                if (user.isActive()) {
                                    Log.d(TAG, "User is active - login successful!");
                                    userLiveData.setValue(user);
                                    updateLastLogin(userId);
                                    logAuditEvent(userId, "USER_LOGIN", "User logged in successfully");
                                } else {
                                    Log.e(TAG, "Login failed: User account is deactivated (isActive = false)");
                                    userLiveData.setValue(null);
                                }
                            } else {
                                Log.e(TAG, "Login failed: User object is null after deserialization");
                                userLiveData.setValue(null);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Login failed: Exception during deserialization", e);
                            userLiveData.setValue(null);
                        }
                    } else {
                        Log.e(TAG, "Login failed: User document does not exist for userId: " + userId);
                        userLiveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Login failed: Error getting user details from Firestore", e);
                    userLiveData.setValue(null);
                });
    }

    public void logout() {
        String userId = firebaseAuth.getCurrentUser() != null ? 
                       firebaseAuth.getCurrentUser().getUid() : "unknown";
        logAuditEvent(userId, "USER_LOGOUT", "User logged out");
        firebaseAuth.signOut();
    }

    public MutableLiveData<Boolean> resetPassword(String email) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    resultLiveData.setValue(task.isSuccessful());
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Password reset email sent");
                    } else {
                        Log.e(TAG, "Password reset failed", task.getException());
                    }
                });
        
        return resultLiveData;
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    private void updateLastLogin(String userId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("lastLogin", new Date());
        
        firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Last login updated"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating last login", e));
    }

    private void logAuditEvent(String userId, String action, String details) {
        Map<String, Object> auditLog = new HashMap<>();
        auditLog.put("userId", userId);
        auditLog.put("action", action);
        auditLog.put("details", details);
        auditLog.put("timestamp", new Date());
        auditLog.put("ipAddress", ""); // Add actual IP if needed
        
        firestore.collection(Constants.COLLECTION_AUDIT_LOGS)
                .add(auditLog)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Audit log created"))
                .addOnFailureListener(e -> Log.e(TAG, "Error creating audit log", e));
    }

    public MutableLiveData<Boolean> deactivateUser(String userId) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("isActive", false);
        
        firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    resultLiveData.setValue(true);
                    logAuditEvent(userId, "USER_DEACTIVATED", "User account deactivated");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deactivating user", e);
                    resultLiveData.setValue(false);
                });
        
        return resultLiveData;
    }

    public MutableLiveData<Boolean> activateUser(String userId) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("isActive", true);
        
        firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    resultLiveData.setValue(true);
                    logAuditEvent(userId, "USER_ACTIVATED", "User account activated");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error activating user", e);
                    resultLiveData.setValue(false);
                });
        
        return resultLiveData;
    }
}
