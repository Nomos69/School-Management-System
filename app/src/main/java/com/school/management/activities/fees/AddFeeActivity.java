package com.school.management.activities.fees;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.R;
import com.school.management.models.Fee;
import com.school.management.utils.DateTimeUtils;

import java.util.Calendar;
import java.util.Date;

public class AddFeeActivity extends AppCompatActivity {
    private TextInputEditText etStudentId, etAmount, etDiscount, etDueDate, etAcademicYear, etTerm, etRemarks;
    private Spinner spinnerFeeType;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private Date selectedDueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fee);

        initializeViews();
    }

    private void initializeViews() {
        firestore = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Fee");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etStudentId = findViewById(R.id.etStudentId);
        etAmount = findViewById(R.id.etAmount);
        etDiscount = findViewById(R.id.etDiscount);
        etDueDate = findViewById(R.id.etDueDate);
        etAcademicYear = findViewById(R.id.etAcademicYear);
        etTerm = findViewById(R.id.etTerm);
        etRemarks = findViewById(R.id.etRemarks);
        spinnerFeeType = findViewById(R.id.spinnerFeeType);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        selectedDueDate = new Date();
        etDueDate.setText(DateTimeUtils.formatDate(selectedDueDate));
        
        etDueDate.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> validateAndSave());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (selectedDueDate != null) {
            calendar.setTime(selectedDueDate);
        }
        
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.set(year, month, dayOfMonth);
            selectedDueDate = selectedCal.getTime();
            etDueDate.setText(DateTimeUtils.formatDate(selectedDueDate));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void validateAndSave() {
        String studentId = etStudentId.getText() != null ? etStudentId.getText().toString().trim() : "";
        String amountStr = etAmount.getText() != null ? etAmount.getText().toString().trim() : "";
        String feeType = spinnerFeeType.getSelectedItem().toString();

        if (studentId.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Please fill in Student ID and Amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            saveFee(studentId, feeType, amount);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFee(String studentId, String feeType, double amount) {
        progressBar.setVisibility(android.view.View.VISIBLE);

        String feeId = firestore.collection("fees").document().getId();
        Fee fee = new Fee(feeId, studentId, feeType, amount);
        
        try {
            String discountStr = etDiscount.getText() != null ? etDiscount.getText().toString().trim() : "0";
            double discount = Double.parseDouble(discountStr);
            fee.setDiscount(discount);
            fee.setTotalAmount(amount - discount);
            fee.setBalanceAmount(amount - discount);
        } catch (NumberFormatException e) {
            fee.setDiscount(0);
            fee.setTotalAmount(amount);
            fee.setBalanceAmount(amount);
        }

        fee.setDueDate(selectedDueDate);
        fee.setAcademicYear(etAcademicYear.getText() != null ? etAcademicYear.getText().toString().trim() : "");
        fee.setTerm(etTerm.getText() != null ? etTerm.getText().toString().trim() : "");
        fee.setRemarks(etRemarks.getText() != null ? etRemarks.getText().toString().trim() : "");

        firestore.collection("fees").document(feeId).set(fee.toMap())
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(AddFeeActivity.this, "Fee created successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(AddFeeActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
