package com.example.prm_group8.controller;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.prm_group8.DBHelper;
import com.example.prm_group8.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddSongActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int PERMISSION_REQUEST_STORAGE = 5;

    private ImageView songImageView;
    private Button selectImageButton;
    private EditText titleEditText;
    private EditText artistEditText;
    private EditText songUrlEditText;
    private Button saveSongButton;
    private DBHelper dbHelper;
    private Uri selectedImageUri;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);

        dbHelper = new DBHelper(this);

        initializeViews();
        setupToolbar();
        setupListeners();
    }

    private void initializeViews() {
        songImageView = findViewById(R.id.songImageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        titleEditText = findViewById(R.id.titleEditText);
        artistEditText = findViewById(R.id.artistEditText);
        songUrlEditText = findViewById(R.id.songUrlEditText);
        saveSongButton = findViewById(R.id.saveSongButton);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Thêm bài hát mới");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void checkPermissionAndOpenImageChooser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_STORAGE);
            } else {
                openImageChooser();
            }
        } else {
            openImageChooser();
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                Toast.makeText(this, "Quyền truy cập bị từ chối. Một số tính năng có thể không hoạt động.", Toast.LENGTH_SHORT).show();
                Log.d("AddSongActivity", "Permission denied for READ_MEDIA_IMAGES");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            getContentResolver().takePersistableUriPermission(selectedImageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
            songImageView.setImageURI(selectedImageUri);
        }
    }

    private void setupListeners() {
        selectImageButton.setOnClickListener(v -> checkPermissionAndOpenImageChooser());
        saveSongButton.setOnClickListener(v -> saveSong());
    }

    private void saveSong() {
        String title = titleEditText.getText().toString().trim();
        String artist = artistEditText.getText().toString().trim();
        String songUrl = songUrlEditText.getText().toString().trim();

        if (title.isEmpty() || artist.isEmpty() || songUrl.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] imageBytes = getImageBytes();
        if (imageBytes == null) {
            imageBytes = getDefaultImageBytes();
        }

        int albumId = 1;
        int duration = 0;

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang lưu bài hát...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        byte[] finalImageBytes = imageBytes;
        executorService.execute(() -> {
            try {
                boolean isSuccess = dbHelper.addSong(title, artist, albumId, duration, songUrl, finalImageBytes);

                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    if (isSuccess) {
                        Toast.makeText(AddSongActivity.this, "Đã lưu bài hát thành công", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AddSongActivity.this, "Lỗi khi lưu bài hát", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddSongActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private byte[] getImageBytes() {
        if (selectedImageUri == null) return null;

        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        } catch (IOException e) {
            Log.e("Error image", "Error converting image to bytes: " + e.getMessage());
            return null;
        }
    }

    private byte[] getDefaultImageBytes() {
        Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_song_image);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        defaultBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}