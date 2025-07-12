package com.example.prm_group8.controller;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_group8.DBHelper;
import com.example.prm_group8.R;
import com.example.prm_group8.UserAdapter;
import com.example.prm_group8.model.User;

import java.util.ArrayList;
import java.util.List;

public class ManagerUserActivity extends AppCompatActivity {

    private static final String TAG = "ManagerUserActivity";
    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set onclick listener cho navigation icon (mũi tên)
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerUserActivity.this, HomeAdminActivity.class);
                startActivity(intent);
            }
        });
        userRecyclerView = findViewById(R.id.songRecyclerView);
        dbHelper = new DBHelper(this);
        setupRecyclerView();
        loadUserData();
    }

    private void setupRecyclerView() {
        userAdapter = new UserAdapter(new ArrayList<>(), new UserAdapter.OnUserDeleteListener() {
            @Override
            public void onUserDelete(User user) {
                deleteUser(user);
            }
        });
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.setAdapter(userAdapter);
    }

    private void loadUserData() {
        new Thread(() -> {
            List<User> users = fetchUsersFromDatabase();
            Log.d(TAG, "Users fetched (including test data): " + users.toString());
            runOnUiThread(() -> {
                if (users.isEmpty()) {
                    showEmptyState();
                } else {
                    hideEmptyState();
                    userAdapter.setUsers(users);
                    Log.d(TAG, "Users set to adapter: " + users.toString());
                }
            });
        }).start();
    }

    private List<User> fetchUsersFromDatabase() {
        List<User> users = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = dbHelper.getUserDetails();
            Log.d(TAG, "Cursor returned from database: " + (cursor != null ? cursor.getCount() : "null"));
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    User user = new User(
                            cursor.getInt(0),    // id
                            cursor.getString(1), // username
                            cursor.getString(2), // password
                            cursor.getString(3), // email
                            cursor.getString(4), // role
                            cursor.getBlob(5),   // image
                            cursor.getInt(6) == 1 // isEmailVerified
                    );
                    users.add(user);
                    Log.d(TAG, "User added: " + user.toString());
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading user data", e);
            runOnUiThread(() -> Toast.makeText(this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return users;
    }

    private void deleteUser(User user) {
        new Thread(() -> {
            boolean success = dbHelper.deleteUser(user.getId());
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                    loadUserData(); // Reload the user list
                } else {
                    Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
    private void showEmptyState() {
        Log.d(TAG, "Showing empty state");
        userRecyclerView.setVisibility(View.GONE);
        findViewById(R.id.emptyStateView).setVisibility(View.VISIBLE);
    }

    private void hideEmptyState() {
        Log.d(TAG, "Hiding empty state");
        userRecyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.emptyStateView).setVisibility(View .GONE);
    }
}