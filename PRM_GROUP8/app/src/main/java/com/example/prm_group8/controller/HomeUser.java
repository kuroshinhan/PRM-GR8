package com.example.prm_group8.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_group8.DBHelper;
import com.example.prm_group8.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HomeUser extends AppCompatActivity {


    private static final String TAG = "HomeUser";
    private RecyclerView recyclerFavorites; // RecyclerView cho bài hát yêu thích
    private int userId;
    private DBHelper dbHelper;
    private ExecutorService executorService;
    private Button btnListeningHistory; // Nút điều hướng đến ListeningHistoryActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);

        executorService = Executors.newSingleThreadExecutor();


        try {
            dbHelper = new DBHelper(this);
            Log.d(TAG, "DBHelper initialized");

            userId = getIntent().getIntExtra("USER_ID", -1);
            if (userId == -1) {
                throw new IllegalArgumentException("No valid user ID provided");
            }
            Log.d(TAG, "User  ID: " + userId);

            initializeViews();
            setupRecyclerViews();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        recyclerFavorites = findViewById(R.id.recyclerFavorites);
        btnListeningHistory = findViewById(R.id.btn_listening_history); // ListeningHistory
        Log.d(TAG, "Views initialized");
    }


    private void setupRecyclerViews() {
        RecyclerView recyclerViewPlaylist = findViewById(R.id.recyclerview);
        recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerFavorites.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        Log.d(TAG, "RecyclerViews set up");
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
            Log.d(TAG, "DBHelper closed");
        }
        if (executorService != null) {
            executorService.shutdown();
            Log.d(TAG, "ExecutorService shutdown");
        }
    }
}