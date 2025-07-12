package com.example.prm_group8.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_group8.DBHelper;
import com.example.prm_group8.R;
import com.example.prm_group8.SongAdapter;
import com.example.prm_group8.model.Song;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ManagerSongActivity extends AppCompatActivity {
    private static final int ADD_SONG_REQUEST_CODE = 1;
    private static final String TAG = "manage_song";

    private RecyclerView songRecyclerView;
    private SearchView searchView;
    private FloatingActionButton addSongFab;
    private TextView emptyStateView;
    private ProgressBar loadingProgressBar;
    private List<Song> songList;
    private SongAdapter songAdapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_song);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadSongs(); // Load songs first
        setupListeners();
    }

    private void initializeViews() {
        songRecyclerView = findViewById(R.id.songRecyclerView);
        searchView = findViewById(R.id.searchView);
        addSongFab = findViewById(R.id.addSongFab);
        emptyStateView = findViewById(R.id.emptyStateView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        songList = new ArrayList<>();
        dbHelper = new DBHelper(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý bài hát");
        }
    }

    private void setupRecyclerView() {
        songAdapter = new SongAdapter(songList, new SongAdapter.OnSongClickListener() {
            @Override
            public void onSongClick(Song song) {
                playSong(song);
            }

            @Override
            public void onSongOptionsClick(Song song, View view) {
                showPopupMenu(song, view);
            }
        });
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songRecyclerView.setAdapter(songAdapter);
    }

    private void playSong(Song song) {
        Intent intent = new Intent(ManagerSongActivity.this, Play_song.class);
        intent.putExtra("song", song);
        startActivity(intent);
    }

    private void loadSongs() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        songList.clear();

        // Lấy danh sách bài hát từ database
        List<Song> songsFromDb = dbHelper.getAllSongs();

        // Nếu không có bài hát nào, thêm bài hát mẫu
        if (songsFromDb.isEmpty()) {

            songsFromDb = dbHelper.getAllSongs(); // Lấy lại danh sách sau khi thêm
        }

        songList.addAll(songsFromDb);
        songAdapter.notifyDataSetChanged();
        loadingProgressBar.setVisibility(View.GONE);
        updateEmptyState();

        Log.d(TAG, "Loaded " + songList.size() + " songs");
    }



    private void showPopupMenu(Song song, View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.inflate(R.menu.song_item_menu);
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit) {
                editSong(song);
                return true;
            } else if (item.getItemId() == R.id.action_delete) {
                deleteSong(song);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void editSong(Song song) {
        // TODO: Implement edit functionality
        Toast.makeText(this, "Edit song: " + song.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void deleteSong(Song song) {
        if (dbHelper.deleteSong(song.getId())) {
            songList.remove(song);
            songAdapter.notifyDataSetChanged();
            updateEmptyState();
            Toast.makeText(this, "Đã xóa: " + song.getTitle(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Không thể xóa bài hát", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterSongs(newText);
                return true;
            }
        });

        addSongFab.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerSongActivity.this, AddSongActivity.class);
            startActivityForResult(intent, ADD_SONG_REQUEST_CODE);
        });
    }

    private void filterSongs(String query) {
        List<Song> filteredList = new ArrayList<>();
        for (Song song : songList) {
            if (song.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    song.getArtist().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(song);
            }
        }
        songAdapter.updateList(filteredList);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (songList.isEmpty()) {
            emptyStateView.setVisibility(View.VISIBLE);
            songRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateView.setVisibility(View.GONE);
            songRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_SONG_REQUEST_CODE && resultCode == RESULT_OK) {
            loadSongs(); // Tải lại danh sách bài hát sau khi thêm thành công
            Toast.makeText(this, "Thêm bài hát thành công", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    // Phương thức này có thể được sử dụng để cập nhật danh sách bài hát từ bên ngoài
    public void refreshSongList() {
        loadSongs();
    }
}