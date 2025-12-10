package com.school.management.activities.library;

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
import com.school.management.models.LibraryBook;

import java.util.UUID;

public class AddBookActivity extends AppCompatActivity {
    private TextInputEditText etTitle, etAuthor, etIsbn, etPublisher, etYear, etTotalCopies, etLocation, etPrice, etBarcode;
    private Spinner spinnerCategory;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        initializeViews();
    }

    private void initializeViews() {
        firestore = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Book");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etTitle = findViewById(R.id.etTitle);
        etAuthor = findViewById(R.id.etAuthor);
        etIsbn = findViewById(R.id.etIsbn);
        etPublisher = findViewById(R.id.etPublisher);
        etYear = findViewById(R.id.etYear);
        etTotalCopies = findViewById(R.id.etTotalCopies);
        etLocation = findViewById(R.id.etLocation);
        etPrice = findViewById(R.id.etPrice);
        etBarcode = findViewById(R.id.etBarcode);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        btnSave.setOnClickListener(v -> saveBook());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveBook() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String author = etAuthor.getText() != null ? etAuthor.getText().toString().trim() : "";
        String isbn = etIsbn.getText() != null ? etIsbn.getText().toString().trim() : "";

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(android.view.View.VISIBLE);

        String bookId = UUID.randomUUID().toString();
        LibraryBook book = new LibraryBook(bookId, title, author, isbn);
        
        book.setCategory(spinnerCategory.getSelectedItem().toString());
        book.setPublisher(etPublisher.getText() != null ? etPublisher.getText().toString().trim() : "");
        
        try {
            int year = Integer.parseInt(etYear.getText() != null ? etYear.getText().toString().trim() : "0");
            book.setPublicationYear(year);
        } catch (NumberFormatException e) {
            book.setPublicationYear(0);
        }

        try {
            int totalCopies = Integer.parseInt(etTotalCopies.getText() != null ? etTotalCopies.getText().toString().trim() : "1");
            book.setTotalCopies(totalCopies);
            book.setAvailableCopies(totalCopies);
        } catch (NumberFormatException e) {
            book.setTotalCopies(1);
            book.setAvailableCopies(1);
        }

        book.setLocation(etLocation.getText() != null ? etLocation.getText().toString().trim() : "");
        
        try {
            double price = Double.parseDouble(etPrice.getText() != null ? etPrice.getText().toString().trim() : "0");
            book.setPrice(price);
        } catch (NumberFormatException e) {
            book.setPrice(0);
        }

        book.setBarcode(etBarcode.getText() != null ? etBarcode.getText().toString().trim() : "");

        firestore.collection("books").document(bookId).set(book.toMap())
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(AddBookActivity.this, "Book added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    Toast.makeText(AddBookActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
