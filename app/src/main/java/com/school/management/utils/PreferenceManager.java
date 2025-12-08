package com.school.management.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static PreferenceManager instance;
    private SharedPreferences sharedPreferences;

    private PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void saveBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void saveInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public void saveUserData(String userId, String userRole, String userEmail, String userName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_USER_ID, userId);
        editor.putString(Constants.PREF_USER_ROLE, userRole);
        editor.putString(Constants.PREF_USER_EMAIL, userEmail);
        editor.putString(Constants.PREF_USER_NAME, userName);
        editor.putBoolean(Constants.PREF_IS_LOGGED_IN, true);
        editor.apply();
    }

    public String getUserId() {
        return getString(Constants.PREF_USER_ID);
    }

    public String getUserRole() {
        return getString(Constants.PREF_USER_ROLE);
    }

    public String getUserEmail() {
        return getString(Constants.PREF_USER_EMAIL);
    }

    public String getUserName() {
        return getString(Constants.PREF_USER_NAME);
    }

    public boolean isLoggedIn() {
        return getBoolean(Constants.PREF_IS_LOGGED_IN);
    }

    public void clearSession() {
        sharedPreferences.edit().clear().apply();
    }
}
