package com.school.management.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class Validators {

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (TextUtils.isEmpty(phone)) return false;
        Pattern pattern = Pattern.compile("^[6-9]\\d{9}$");
        return pattern.matcher(phone).matches();
    }

    public static boolean isValidPassword(String password) {
        // At least 8 characters, one uppercase, one lowercase, one digit
        if (TextUtils.isEmpty(password) || password.length() < 8) return false;
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
        return pattern.matcher(password).matches();
    }

    public static boolean isValidName(String name) {
        if (TextUtils.isEmpty(name)) return false;
        return name.trim().length() >= 2;
    }

    public static boolean isValidRollNumber(String rollNumber) {
        return !TextUtils.isEmpty(rollNumber) && rollNumber.trim().length() > 0;
    }

    public static boolean isValidAmount(String amount) {
        if (TextUtils.isEmpty(amount)) return false;
        try {
            double value = Double.parseDouble(amount);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidDate(String date, String format) {
        if (TextUtils.isEmpty(date)) return false;
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isValidISBN(String isbn) {
        if (TextUtils.isEmpty(isbn)) return false;
        // ISBN-10 or ISBN-13
        Pattern pattern = Pattern.compile("^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$");
        return pattern.matcher(isbn).matches();
    }

    public static boolean isValidZipCode(String zipCode) {
        if (TextUtils.isEmpty(zipCode)) return false;
        Pattern pattern = Pattern.compile("^[1-9][0-9]{5}$");
        return pattern.matcher(zipCode).matches();
    }

    public static String getPasswordStrength(String password) {
        if (TextUtils.isEmpty(password)) return "Very Weak";
        
        int strength = 0;
        if (password.length() >= 8) strength++;
        if (password.matches(".*[a-z].*")) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*\\d.*")) strength++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) strength++;

        switch (strength) {
            case 0:
            case 1:
                return "Very Weak";
            case 2:
                return "Weak";
            case 3:
                return "Medium";
            case 4:
                return "Strong";
            case 5:
                return "Very Strong";
            default:
                return "Unknown";
        }
    }
}
