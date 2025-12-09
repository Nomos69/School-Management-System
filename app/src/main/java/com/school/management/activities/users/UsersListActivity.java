package com.school.management.activities.users;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.school.management.R;
import com.school.management.adapters.UserAdapter;
import com.school.management.models.User;
import com.school.management.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class UsersListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private List<User> filteredList;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddUser;
    
    private FirebaseFirestore firestore;
    private String currentFilter = "ALL"; // ALL, ADMIN, TEACHER, STUDENT, PARENT, STAFF
    private String sortBy = "fullName"; // fullName, email, role, createdAt

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        
        firestore = FirebaseFirestore.getInstance();
        
        setupToolbar();
        initializeViews();
        setupRecyclerView();
        setupFAB();
        loadUsers();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Users");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        fabAddUser = findViewById(R.id.fabAddUser);
        
        userList = new ArrayList<>();
        filteredList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(this, filteredList, this::onUserClick);
        recyclerView.setAdapter(userAdapter);
    }

    private void setupFAB() {
        fabAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(UsersListActivity.this, RegisterUserActivity.class);
            startActivity(intent);
        });
    }

    private void loadUsers() {
        showProgress(true);
        
        Query query = firestore.collection(Constants.COLLECTION_USERS)
                .orderBy(sortBy, Query.Direction.ASCENDING);
        
        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        userList.add(user);
                    }
                    applyFilter();
                    showProgress(false);
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(this, "Error loading users: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void applyFilter() {
        filteredList.clear();
        
        for (User user : userList) {
            if (currentFilter.equals("ALL") || user.getRole().equals(currentFilter)) {
                filteredList.add(user);
            }
        }
        
        userAdapter.notifyDataSetChanged();
    }

    private void onUserClick(User user) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("USER_ID", user.getUserId());
        startActivity(intent);
    }

    private void showFilterDialog() {
        String[] filters = {"All Users", "Admins", "Teachers", "Students", "Parents", "Staff"};
        String[] filterValues = {"ALL", "ADMIN", "TEACHER", "STUDENT", "PARENT", "STAFF"};
        
        new AlertDialog.Builder(this)
                .setTitle("Filter by Role")
                .setItems(filters, (dialog, which) -> {
                    currentFilter = filterValues[which];
                    applyFilter();
                })
                .show();
    }

    private void showSortDialog() {
        String[] sortOptions = {"Name", "Email", "Role", "Date Created"};
        String[] sortFields = {"fullName", "email", "role", "createdAt"};
        
        new AlertDialog.Builder(this)
                .setTitle("Sort by")
                .setItems(sortOptions, (dialog, which) -> {
                    sortBy = sortFields[which];
                    loadUsers();
                })
                .show();
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_users_list, menu);
        
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBySearch(newText);
                return true;
            }
        });
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_filter) {
            showFilterDialog();
            return true;
        } else if (id == R.id.action_sort) {
            showSortDialog();
            return true;
        } else if (id == R.id.action_refresh) {
            loadUsers();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void filterBySearch(String query) {
        filteredList.clear();
        
        if (query.isEmpty()) {
            applyFilter();
            return;
        }
        
        String lowerCaseQuery = query.toLowerCase();
        for (User user : userList) {
            if ((currentFilter.equals("ALL") || user.getRole().equals(currentFilter)) &&
                (user.getFullName().toLowerCase().contains(lowerCaseQuery) ||
                 user.getEmail().toLowerCase().contains(lowerCaseQuery) ||
                 user.getRole().toLowerCase().contains(lowerCaseQuery))) {
                filteredList.add(user);
            }
        }
        
        userAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers(); // Refresh list when returning from other activities
    }
}
