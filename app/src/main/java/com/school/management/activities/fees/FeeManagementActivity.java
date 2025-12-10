package com.school.management.activities.fees;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.R;
import com.school.management.adapters.FeeAdapter;
import com.school.management.models.Fee;

import java.util.ArrayList;
import java.util.List;

public class FeeManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FeeAdapter adapter;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private FloatingActionButton fabAddFee;
    private List<Fee> feeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fee_management);
        
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadFees();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        firestore = FirebaseFirestore.getInstance();
        fabAddFee = findViewById(R.id.fabAddFee);
        
        fabAddFee.setOnClickListener(v -> {
            startActivity(new Intent(this, AddFeeActivity.class));
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Fee Management");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FeeAdapter(this, feeList, fee -> {
            Intent intent = new Intent(this, FeeDetailActivity.class);
            intent.putExtra("feeId", fee.getFeeId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadFees() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        try {
            firestore.collection("fees")
                    .orderBy("dueDate", com.google.firebase.firestore.Query.Direction.ASCENDING)
                    .addSnapshotListener((value, error) -> {
                        progressBar.setVisibility(android.view.View.GONE);
                        if (error != null) {
                            android.util.Log.e("FeeManagementActivity", "Error loading fees", error);
                            Toast.makeText(FeeManagementActivity.this, "Error loading fees", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        feeList.clear();
                        if (value != null) {
                            for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                                Fee fee = doc.toObject(Fee.class);
                                if (fee != null) {
                                    feeList.add(fee);
                                }
                            }
                        }
                        adapter.updateList(feeList);
                    });
        } catch (Exception e) {
            progressBar.setVisibility(android.view.View.GONE);
            android.util.Log.e("FeeManagementActivity", "Exception loading fees", e);
            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFees();
    }
}
