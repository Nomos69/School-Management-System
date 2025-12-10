package com.school.management.activities.communication;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.school.management.R;
import com.school.management.adapters.MessageAdapter;
import com.school.management.models.Message;
import com.school.management.repositories.MessageRepository;

import java.util.ArrayList;
import java.util.List;

public class MessagingActivity extends AppCompatActivity implements MessageAdapter.OnMessageClickListener {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private ProgressBar progressBar;
    private MessageRepository messageRepository;
    private List<Message> messageList;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        firebaseAuth = FirebaseAuth.getInstance();
        messageRepository = new MessageRepository();
        messageList = new ArrayList<>();

        currentUserId = firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getUid() : null;

        setupToolbar();
        initializeViews();
        setupRecyclerView();
        
        if (currentUserId != null) {
            loadMessages();
        }
    }

    private void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Messages");
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
            adapter = new MessageAdapter(this, messageList, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error setting up RecyclerView: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMessages() {
        try {
            if (progressBar != null) {
                progressBar.setVisibility(android.view.View.VISIBLE);
            }

            messageRepository.getMessagesForUser(currentUserId, new MessageRepository.OnMessageListListener() {
                @Override
                public void onSuccess(List<Message> messages) {
                    if (progressBar != null) {
                        progressBar.setVisibility(android.view.View.GONE);
                    }
                    adapter.updateList(messages);
                }

                @Override
                public void onFailure(String error) {
                    if (progressBar != null) {
                        progressBar.setVisibility(android.view.View.GONE);
                    }
                    Toast.makeText(MessagingActivity.this, "Failed to load messages: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading messages: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMessageClick(Message message) {
        try {
            // Mark message as read
            if (!message.isRead()) {
                messageRepository.markAsRead(message.getMessageId(), new MessageRepository.OnCompleteListener() {
                    @Override
                    public void onSuccess() {
                        // Message marked as read
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(MessagingActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // TODO: Open message detail activity
            Toast.makeText(this, "Message from: " + message.getSenderId(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
