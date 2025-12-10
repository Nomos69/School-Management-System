package com.school.management.activities.events;

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
import com.school.management.adapters.EventAdapter;
import com.school.management.models.Event;

import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private FloatingActionButton fabAddEvent;
    private List<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadEvents();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        firestore = FirebaseFirestore.getInstance();
        fabAddEvent = findViewById(R.id.fabAddEvent);
        
        fabAddEvent.setOnClickListener(v -> {
            startActivity(new Intent(this, AddEventActivity.class));
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Events");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(this, eventList, event -> {
            Intent intent = new Intent(this, EventDetailActivity.class);
            intent.putExtra("eventId", event.getEventId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadEvents() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        try {
            firestore.collection("events")
                    .orderBy("startDate")
                    .addSnapshotListener((value, error) -> {
                        progressBar.setVisibility(android.view.View.GONE);
                        if (error != null) {
                            android.util.Log.e("EventsActivity", "Error loading events", error);
                            Toast.makeText(EventsActivity.this, "Error loading events", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        eventList.clear();
                        if (value != null) {
                            for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                                Event event = doc.toObject(Event.class);
                                if (event != null) {
                                    eventList.add(event);
                                }
                            }
                        }
                        adapter.updateList(eventList);
                    });
        } catch (Exception e) {
            progressBar.setVisibility(android.view.View.GONE);
            android.util.Log.e("EventsActivity", "Exception loading events", e);
            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }
}
