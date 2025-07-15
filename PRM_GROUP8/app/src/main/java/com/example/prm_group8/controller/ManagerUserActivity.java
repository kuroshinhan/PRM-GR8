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
        userRecyclerView = findViewById(R.id.songRecyclerView); // Lưu ý: Có thể cần sửa thành userRecyclerView
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
        try {
            users = dbHelper.getAllUsers(); // Sử dụng getAllUsers() thay vì getUserDetails()
            Log.d(TAG, "Users retrieved from database: " + (users != null ? users.size() : "null"));
        } catch (Exception e) {
            Log.e(TAG, "Error loading user data", e);
            runOnUiThread(() -> Toast.makeText(this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_LONG).show());
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
        findViewById(R.id.emptyStateView).setVisibility(View.GONE);
    }
}