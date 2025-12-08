package com.school.management.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.school.management.R;
import com.school.management.activities.admin.SystemSettingsActivity;
import com.school.management.activities.attendance.AttendanceActivity;
import com.school.management.activities.auth.LoginActivity;
import com.school.management.activities.classes.ClassListActivity;
import com.school.management.activities.communication.MessagingActivity;
import com.school.management.activities.events.EventsActivity;
import com.school.management.activities.examination.ExamListActivity;
import com.school.management.activities.fees.FeeManagementActivity;
import com.school.management.activities.library.LibraryActivity;
import com.school.management.activities.parent.ParentDashboardActivity;
import com.school.management.activities.students.StudentListActivity;
import com.school.management.activities.teachers.TeacherListActivity;
import com.school.management.activities.users.UsersListActivity;
import com.school.management.repositories.AuthRepository;
import com.school.management.utils.Constants;
import com.school.management.utils.PreferenceManager;

public class MainActivity extends AppCompatActivity 
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private PreferenceManager preferenceManager;
    private AuthRepository authRepository;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceManager = PreferenceManager.getInstance(this);
        authRepository = new AuthRepository();
        userRole = preferenceManager.getUserRole();

        setupToolbar();
        setupNavigationDrawer();
        setupDashboardCards();
        updateNavigationHeader();
        configureNavigationMenuByRole();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.toolbar),
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupDashboardCards() {
        MaterialCardView cardUsers = findViewById(R.id.cardUsers);
        MaterialCardView cardStudents = findViewById(R.id.cardStudents);
        MaterialCardView cardTeachers = findViewById(R.id.cardTeachers);
        MaterialCardView cardClasses = findViewById(R.id.cardClasses);
        MaterialCardView cardAttendance = findViewById(R.id.cardAttendance);
        MaterialCardView cardExams = findViewById(R.id.cardExams);
        MaterialCardView cardFees = findViewById(R.id.cardFees);
        MaterialCardView cardLibrary = findViewById(R.id.cardLibrary);
        MaterialCardView cardEvents = findViewById(R.id.cardEvents);
        MaterialCardView cardMessages = findViewById(R.id.cardMessages);

        cardUsers.setOnClickListener(v -> startActivity(new Intent(this, UsersListActivity.class)));
        cardStudents.setOnClickListener(v -> startActivity(new Intent(this, StudentListActivity.class)));
        cardTeachers.setOnClickListener(v -> startActivity(new Intent(this, TeacherListActivity.class)));
        cardClasses.setOnClickListener(v -> startActivity(new Intent(this, ClassListActivity.class)));
        cardAttendance.setOnClickListener(v -> startActivity(new Intent(this, AttendanceActivity.class)));
        cardExams.setOnClickListener(v -> startActivity(new Intent(this, ExamListActivity.class)));
        cardFees.setOnClickListener(v -> startActivity(new Intent(this, FeeManagementActivity.class)));
        cardLibrary.setOnClickListener(v -> startActivity(new Intent(this, LibraryActivity.class)));
        cardEvents.setOnClickListener(v -> startActivity(new Intent(this, EventsActivity.class)));
        cardMessages.setOnClickListener(v -> startActivity(new Intent(this, MessagingActivity.class)));

        // Hide cards based on user role
        configureCardVisibilityByRole();
    }

    private void updateNavigationHeader() {
        if (navigationView.getHeaderCount() > 0) {
            TextView tvUserName = navigationView.getHeaderView(0).findViewById(R.id.tvUserName);
            TextView tvUserEmail = navigationView.getHeaderView(0).findViewById(R.id.tvUserEmail);
            TextView tvUserRole = navigationView.getHeaderView(0).findViewById(R.id.tvUserRole);

            tvUserName.setText(preferenceManager.getUserName());
            tvUserEmail.setText(preferenceManager.getUserEmail());
            tvUserRole.setText(userRole);
        }
    }

    private void configureCardVisibilityByRole() {
        // All cards visible for admin
        if (Constants.ROLE_ADMIN.equals(userRole)) {
            return;
        }

        // Configure for other roles
        MaterialCardView cardUsers = findViewById(R.id.cardUsers);

        if (Constants.ROLE_TEACHER.equals(userRole)) {
            cardUsers.setVisibility(android.view.View.GONE);
        } else if (Constants.ROLE_PARENT.equals(userRole)) {
            // Parent should see limited cards
            startActivity(new Intent(this, ParentDashboardActivity.class));
            finish();
        }
    }

    private void configureNavigationMenuByRole() {
        Menu menu = navigationView.getMenu();
        
        if (!Constants.ROLE_ADMIN.equals(userRole)) {
            menu.findItem(R.id.nav_system_settings).setVisible(false);
            menu.findItem(R.id.nav_users).setVisible(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Already on dashboard
        } else if (id == R.id.nav_users) {
            startActivity(new Intent(this, UsersListActivity.class));
        } else if (id == R.id.nav_students) {
            startActivity(new Intent(this, StudentListActivity.class));
        } else if (id == R.id.nav_teachers) {
            startActivity(new Intent(this, TeacherListActivity.class));
        } else if (id == R.id.nav_classes) {
            startActivity(new Intent(this, ClassListActivity.class));
        } else if (id == R.id.nav_attendance) {
            startActivity(new Intent(this, AttendanceActivity.class));
        } else if (id == R.id.nav_exams) {
            startActivity(new Intent(this, ExamListActivity.class));
        } else if (id == R.id.nav_fees) {
            startActivity(new Intent(this, FeeManagementActivity.class));
        } else if (id == R.id.nav_library) {
            startActivity(new Intent(this, LibraryActivity.class));
        } else if (id == R.id.nav_events) {
            startActivity(new Intent(this, EventsActivity.class));
        } else if (id == R.id.nav_messages) {
            startActivity(new Intent(this, MessagingActivity.class));
        } else if (id == R.id.nav_system_settings) {
            startActivity(new Intent(this, SystemSettingsActivity.class));
        } else if (id == R.id.nav_logout) {
            logout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        authRepository.logout();
        preferenceManager.clearSession();
        
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_notifications) {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_profile) {
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
