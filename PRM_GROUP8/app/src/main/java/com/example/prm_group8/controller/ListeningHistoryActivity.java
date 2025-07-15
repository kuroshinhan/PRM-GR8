package com.example.prm_group8.controller;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_group8.R;
import com.example.prm_group8.adapter.ListeningHistoryAdapter;
import com.example.prm_group8.database.DBHelper;
import com.example.prm_group8.model.Song;

import java.util.ArrayList;
import java.util.List;

public class ListeningHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListeningHistoryAdapter adapter;
    private List<DBHelper.ListeningHistoryItem> listeningHistoryItems;
    private DBHelper dbHelper; // Đối tượng DBHelper để truy cập cơ sở dữ liệu
    private int userId; // ID của người dùng (có thể lấy từ SharedPreferences hoặc Intent)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening_history);

        // Khởi tạo DBHelper
        dbHelper = new DBHelper(this);

        // Lấy userId từ Intent
        userId = getIntent().getIntExtra("USER_ID", -1); // Thay -1 bằng giá trị mặc định nếu cần

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lấy danh sách lịch sử nghe của người dùng
        listeningHistoryItems = dbHelper.getUserListeningHistory(userId); // Gọi phương thức

        // Lấy danh sách bài hát từ lịch sử nghe
        List<Song> songList = new ArrayList<>();
        for (DBHelper.ListeningHistoryItem item : listeningHistoryItems) {
            Song song = dbHelper.getSongById(item.getSongId()); // Lấy bài hát từ ID
            if (song != null) {
                songList.add(song); // Thêm bài hát vào danh sách
            }
        }

        // Tạo Adapter và gán vào RecyclerView
        adapter = new ListeningHistoryAdapter(songList, this, userId); // Truyền danh sách bài hát và context vào Adapter
        recyclerView.setAdapter(adapter);

        // Khởi tạo nút quay lại
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed()); // Quay lại màn hình trước
    }
}
