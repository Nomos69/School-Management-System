package com.school.management.activities.library;

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
import com.school.management.adapters.BookAdapter;
import com.school.management.models.LibraryBook;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private FloatingActionButton fabAddBook;
    private List<LibraryBook> bookList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadBooks();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        firestore = FirebaseFirestore.getInstance();
        fabAddBook = findViewById(R.id.fabAddBook);
        
        fabAddBook.setOnClickListener(v -> {
            startActivity(new Intent(this, AddBookActivity.class));
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Library");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookAdapter(this, bookList, book -> {
            Intent intent = new Intent(this, BookDetailActivity.class);
            intent.putExtra("bookId", book.getBookId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadBooks() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        try {
            firestore.collection("books")
                    .orderBy("title")
                    .addSnapshotListener((value, error) -> {
                        progressBar.setVisibility(android.view.View.GONE);
                        if (error != null) {
                            android.util.Log.e("LibraryActivity", "Error loading books", error);
                            Toast.makeText(LibraryActivity.this, "Error loading books", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        bookList.clear();
                        if (value != null) {
                            for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                                LibraryBook book = doc.toObject(LibraryBook.class);
                                if (book != null) {
                                    bookList.add(book);
                                }
                            }
                        }
                        adapter.updateList(bookList);
                    });
        } catch (Exception e) {
            progressBar.setVisibility(android.view.View.GONE);
            android.util.Log.e("LibraryActivity", "Exception loading books", e);
            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBooks();
    }
}
