package com.school.management.activities.fees;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.R;
import com.school.management.models.Fee;
import com.school.management.utils.DateTimeUtils;

public class FeeDetailActivity extends AppCompatActivity {
    private TextInputEditText etStudentId, etAmount, etPaidAmount, etBalanceAmount, etDueDate;
    private Spinner spinnerPaymentMethod;
    private TextView tvStatus, tvFeeType, tvDiscount;
    private Button btnRecordPayment, btnDelete, btnClose;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private Fee currentFee;
    private String feeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fee_detail);

        feeId = getIntent().getStringExtra("feeId");
        initializeViews();
        setupToolbar();
        loadFeeDetails();
    }

    private void initializeViews() {
        firestore = FirebaseFirestore.getInstance();

        etStudentId = findViewById(R.id.etStudentId);
        etAmount = findViewById(R.id.etAmount);
        etPaidAmount = findViewById(R.id.etPaidAmount);
        etBalanceAmount = findViewById(R.id.etBalanceAmount);
        etDueDate = findViewById(R.id.etDueDate);
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        tvStatus = findViewById(R.id.tvStatus);
        tvFeeType = findViewById(R.id.tvFeeType);
        tvDiscount = findViewById(R.id.tvDiscount);

        btnRecordPayment = findViewById(R.id.btnRecordPayment);
        btnDelete = findViewById(R.id.btnDelete);
        btnClose = findViewById(R.id.btnClose);
        progressBar = findViewById(R.id.progressBar);

        // Set fields as read-only
        etStudentId.setEnabled(false);
        etAmount.setEnabled(false);
        etPaidAmount.setEnabled(false);
        etBalanceAmount.setEnabled(false);
        etDueDate.setEnabled(false);

        btnRecordPayment.setOnClickListener(v -> showRecordPaymentDialog());
        btnDelete.setOnClickListener(v -> showDeleteConfirmDialog());
        btnClose.setOnClickListener(v -> finish());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Fee Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadFeeDetails() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        firestore.collection("fees").document(feeId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    if (error != null) {
                        Toast.makeText(FeeDetailActivity.this, "Error loading fee", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        currentFee = documentSnapshot.toObject(Fee.class);
                        displayFeeDetails();
                    }
                });
    }

    private void displayFeeDetails() {
        if (currentFee == null) return;

        etStudentId.setText(currentFee.getStudentId());
        etAmount.setText(String.format("%.2f", currentFee.getAmount()));
        etPaidAmount.setText(String.format("%.2f", currentFee.getPaidAmount()));
        etBalanceAmount.setText(String.format("%.2f", currentFee.getBalanceAmount()));
        etDueDate.setText(DateTimeUtils.formatDate(currentFee.getDueDate()));
        
        tvFeeType.setText("Fee Type: " + currentFee.getFeeType());
        tvDiscount.setText(String.format("Discount: ₹%.2f", currentFee.getDiscount()));
        
        tvStatus.setText(currentFee.getStatus());
        int statusColor = getStatusColor(currentFee.getStatus());
        tvStatus.setTextColor(getResources().getColor(statusColor));
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "PAID":
                return R.color.payment_paid;
            case "PARTIAL":
                return R.color.payment_partial;
            case "PENDING":
                return R.color.payment_pending;
            case "OVERDUE":
                return R.color.payment_overdue;
            default:
                return R.color.text_secondary;
        }
    }

    private void showRecordPaymentDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Record Payment");

        TextInputEditText etPaymentAmount = new TextInputEditText(this);
        etPaymentAmount.setHint("Payment Amount");
        builder.setView(etPaymentAmount);

        builder.setPositiveButton("Record", (dialog, which) -> {
            String paymentStr = etPaymentAmount.getText() != null ? etPaymentAmount.getText().toString().trim() : "";
            if (!paymentStr.isEmpty()) {
                try {
                    double paymentAmount = Double.parseDouble(paymentStr);
                    recordPayment(paymentAmount);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void recordPayment(double paymentAmount) {
        progressBar.setVisibility(android.view.View.VISIBLE);

        double newPaidAmount = currentFee.getPaidAmount() + paymentAmount;
        double newBalanceAmount = currentFee.getTotalAmount() - newPaidAmount;
        
        String newStatus;
        if (newBalanceAmount <= 0) {
            newStatus = "PAID";
            newBalanceAmount = 0;
        } else if (newPaidAmount > 0) {
            newStatus = "PARTIAL";
        } else {
            newStatus = currentFee.getStatus();
        }

        currentFee.setPaidAmount(newPaidAmount);
        currentFee.setBalanceAmount(Math.max(0, newBalanceAmount));
        currentFee.setStatus(newStatus);
        currentFee.setPaymentMethod(spinnerPaymentMethod.getSelectedItem().toString());
        currentFee.setPaidDate(new java.util.Date());

        firestore.collection("fees").document(feeId).set(currentFee.toMap())
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(FeeDetailActivity.this, "Payment recorded successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(FeeDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteConfirmDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Fee")
                .setMessage("Are you sure you want to delete this fee record?")
                .setPositiveButton("Delete", (dialog, which) -> deleteFee())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteFee() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        firestore.collection("fees").document(feeId).delete()
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(FeeDetailActivity.this, "Fee deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(FeeDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
