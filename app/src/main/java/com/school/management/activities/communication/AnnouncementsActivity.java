package com.school.management.activities.communication;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.school.management.R;
import com.school.management.adapters.AnnouncementAdapter;
import com.school.management.models.Announcement;
import com.school.management.repositories.AnnouncementRepository;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementsActivity extends AppCompatActivity implements AnnouncementAdapter.OnAnnouncementClickListener {
    private RecyclerView recyclerView;
    private AnnouncementAdapter adapter;
    private ProgressBar progressBar;
    private AnnouncementRepository announcementRepository;
    private List<Announcement> announcementList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);

        announcementRepository = new AnnouncementRepository();
        announcementList = new ArrayList<>();

        setupToolbar();
        initializeViews();
        setupRecyclerView();
        loadAnnouncements();
    }

    private void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Announcements");
            }
            if (toolbar != null) {
                toolbar.setNavigationOnClickListener(v -> onBackPressed());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeViews() {
        try {
            recyclerView = findViewById(R.id.recyclerView);
            progressBar = findViewById(R.id.progressBar);

            if (recyclerView == null) {
                throw new RuntimeException("RecyclerView not found in layout");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        try {
            adapter = new AnnouncementAdapter(this, announcementList, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error setting up RecyclerView: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAnnouncements() {
        try {
            if (progressBar != null) {
                progressBar.setVisibility(android.view.View.VISIBLE);
            }

            announcementRepository.getAllAnnouncements(new AnnouncementRepository.OnAnnouncementListListener() {
                @Override
                public void onSuccess(List<Announcement> announcements) {
                    if (progressBar != null) {
                        progressBar.setVisibility(android.view.View.GONE);
                    }
                    adapter.updateList(announcements);
                }

                @Override
                public void onFailure(String error) {
                    if (progressBar != null) {
                        progressBar.setVisibility(android.view.View.GONE);
                    }
                    Toast.makeText(AnnouncementsActivity.this, "Failed to load announcements: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading announcements: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAnnouncementClick(Announcement announcement) {
        try {
            // TODO: Open announcement detail activity
            Toast.makeText(this, "Announcement: " + announcement.getTitle(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
