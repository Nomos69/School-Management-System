package com.school.management.activities.library;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.school.management.R;
import com.school.management.models.LibraryBook;

public class BookDetailActivity extends AppCompatActivity {
    private TextInputEditText etTitle, etAuthor, etIsbn, etPublisher, etYear, etTotalCopies, etAvailableCopies, etLocation, etPrice;
    private TextView tvAvailability;
    private Button btnIssueBook, btnDelete, btnClose;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private LibraryBook currentBook;
    private String bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        bookId = getIntent().getStringExtra("bookId");
        initializeViews();
        setupToolbar();
        loadBookDetails();
    }

    private void initializeViews() {
        firestore = FirebaseFirestore.getInstance();

        etTitle = findViewById(R.id.etTitle);
        etAuthor = findViewById(R.id.etAuthor);
        etIsbn = findViewById(R.id.etIsbn);
        etPublisher = findViewById(R.id.etPublisher);
        etYear = findViewById(R.id.etYear);
        etTotalCopies = findViewById(R.id.etTotalCopies);
        etAvailableCopies = findViewById(R.id.etAvailableCopies);
        etLocation = findViewById(R.id.etLocation);
        etPrice = findViewById(R.id.etPrice);
        tvAvailability = findViewById(R.id.tvAvailability);

        btnIssueBook = findViewById(R.id.btnIssueBook);
        btnDelete = findViewById(R.id.btnDelete);
        btnClose = findViewById(R.id.btnClose);
        progressBar = findViewById(R.id.progressBar);

        // Set fields as read-only
        etTitle.setEnabled(false);
        etAuthor.setEnabled(false);
        etIsbn.setEnabled(false);
        etPublisher.setEnabled(false);
        etYear.setEnabled(false);
        etTotalCopies.setEnabled(false);
        etAvailableCopies.setEnabled(false);
        etLocation.setEnabled(false);
        etPrice.setEnabled(false);

        btnIssueBook.setOnClickListener(v -> showIssueBookDialog());
        btnDelete.setOnClickListener(v -> showDeleteConfirmDialog());
        btnClose.setOnClickListener(v -> finish());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Book Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadBookDetails() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        firestore.collection("books").document(bookId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    if (error != null) {
                        Toast.makeText(BookDetailActivity.this, "Error loading book", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        currentBook = documentSnapshot.toObject(LibraryBook.class);
                        displayBookDetails();
                    }
                });
    }

    private void displayBookDetails() {
        if (currentBook == null) return;

        etTitle.setText(currentBook.getTitle());
        etAuthor.setText(currentBook.getAuthor());
        etIsbn.setText(currentBook.getIsbn());
        etPublisher.setText(currentBook.getPublisher());
        etYear.setText(String.valueOf(currentBook.getPublicationYear()));
        etTotalCopies.setText(String.valueOf(currentBook.getTotalCopies()));
        etAvailableCopies.setText(String.valueOf(currentBook.getAvailableCopies()));
        etLocation.setText(currentBook.getLocation());
        etPrice.setText(String.format("₹%.2f", currentBook.getPrice()));

        if (currentBook.getAvailableCopies() > 0) {
            tvAvailability.setText("Available");
            tvAvailability.setTextColor(getResources().getColor(R.color.success));
            btnIssueBook.setEnabled(true);
        } else {
            tvAvailability.setText("Not Available");
            tvAvailability.setTextColor(getResources().getColor(R.color.error));
            btnIssueBook.setEnabled(false);
        }
    }

    private void showIssueBookDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Issue Book")
                .setMessage("Issue a copy of '" + currentBook.getTitle() + "'?")
                .setPositiveButton("Issue", (dialog, which) -> issueBook())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void issueBook() {
        if (currentBook == null || currentBook.getAvailableCopies() <= 0) {
            Toast.makeText(this, "Book not available", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(android.view.View.VISIBLE);
        currentBook.setAvailableCopies(currentBook.getAvailableCopies() - 1);

        firestore.collection("books").document(bookId).set(currentBook.toMap())
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(BookDetailActivity.this, "Book issued successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(BookDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteConfirmDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Book")
                .setMessage("Are you sure you want to delete this book?")
                .setPositiveButton("Delete", (dialog, which) -> deleteBook())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteBook() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        firestore.collection("books").document(bookId).delete()
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(BookDetailActivity.this, "Book deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(BookDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
