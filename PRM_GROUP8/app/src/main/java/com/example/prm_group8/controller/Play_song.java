package com.example.prm_group8.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.example.prm_group8.DBHelper;
import com.example.prm_group8.R;
import com.example.prm_group8.model.Song;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Play_song extends AppCompatActivity {

    private static final String TAG = "Play_song";
    private TextView titleTv, artistTv, currentTimeTv, totalTimeTv;
    private SeekBar seekBar;
    private ImageView pausePlay, nextBtn, previousBtn, musicIcon, btnBack, favoriteButton;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;
    private boolean isPlaying = false;
    private boolean isFavorite = false;

    private List<Song> playlist;
    private int currentSongIndex;
    private int userId;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_song);

        dbHelper = new DBHelper(this);
        initializeViews();
        setupClickListeners();
        getIntentData();
    }

    private void initializeViews() {
        titleTv = findViewById(R.id.song_title);
        artistTv = findViewById(R.id.artist_name);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.music_icon_big);
        btnBack = findViewById(R.id.btn_back);
        favoriteButton = findViewById(R.id.favoriteButton); // Khởi tạo nút yêu thích

        pausePlay.setImageResource(R.drawable.baseline_play_circle_outline_24);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
        nextBtn.setOnClickListener(v -> playNextSong());
        previousBtn.setOnClickListener(v -> playPreviousSong());
        pausePlay.setOnClickListener(v -> togglePlayPause());
        favoriteButton.setOnClickListener(v -> toggleFavorite()); // Thêm sự kiện cho nút yêu thích
    }

    private void getIntentData() {
        currentSongIndex = getIntent().getIntExtra("position", -1);
        userId = getIntent().getIntExtra("USER_ID", -1);

        if (currentSongIndex == -1 || userId == -1) {
            Log.e(TAG, "Invalid song position or user ID");
            Toast.makeText(this, "Error: Invalid data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPlaylist();
    }
    private void playNextSong() {
        if (currentSongIndex < playlist.size() - 1) {
            currentSongIndex++;
        } else {
            currentSongIndex = 0;
        }
        playSong();
    }
    private void playPreviousSong() {
        if (currentSongIndex > 0) {
            currentSongIndex--;
        } else {
            currentSongIndex = playlist.size() - 1;
        }
        playSong();
    }
    private void loadPlaylist() {
        playlist = dbHelper.getAllSongs();
        if (playlist == null || playlist.isEmpty() || currentSongIndex >= playlist.size()) {
            Log.e(TAG, "Invalid playlist data or index");
            Toast.makeText(this, "Error: Invalid playlist data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        playSong();
    }

    private void playSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Song song = playlist.get(currentSongIndex);
        updateSongInfo(song);
        setupMediaPlayer(song);
        updateFavoriteStatus(); // Cập nhật trạng thái yêu thích
    }

    private void updateSongInfo(Song song) {
        titleTv.setText(song.getTitle());
        artistTv.setText(song.getArtist());
        updateMusicIcon(song);
    }

    private void updateMusicIcon(Song song) {
        byte[] imageData = song.getImage();
        if (imageData != null && imageData.length > 0) {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                roundedBitmapDrawable.setCircular(true);
                musicIcon.setImageDrawable(roundedBitmapDrawable);
            } catch (Exception e) {
                Log.e(TAG, "Error loading image", e);
                musicIcon.setImageResource(R.drawable.default_song_image);
            }
        } else {
            musicIcon.setImageResource(R.drawable.default_song_image);
        }
    }

    private void setupMediaPlayer(Song song) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        try {
            mediaPlayer.setDataSource(song.getSongUrl());
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                totalTimeTv.setText(formatTime(mediaPlayer.getDuration()));
                seekBar.setMax(mediaPlayer.getDuration());
                startPlayback();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
                Toast.makeText(Play_song.this, "Error playing audio", Toast.LENGTH_SHORT).show();
                return false;
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                pausePlay.setImageResource(R.drawable.baseline_play_circle_outline_24);
                stopUpdateSeekBarProgress();
                isPlaying = false;
                playNextSong();
            });

        } catch (IOException e) {
            Log.e(TAG, "Error setting data source", e);
            Toast.makeText(this, "Error playing song", Toast.LENGTH_SHORT).show();
        }

        setupSeekBar();
    }

    private void startPlayback() {
        mediaPlayer.start();
        isPlaying = true;
        pausePlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
        startUpdateSeekBarProgress();
    }

    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser ) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    currentTimeTv.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopUpdateSeekBarProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    startUpdateSeekBarProgress();
                }
            }
        });
    }

    private void togglePlayPause() {
        if (mediaPlayer != null) {
            if (isPlaying) {
                mediaPlayer.pause();
                pausePlay.setImageResource(R.drawable.baseline_play_circle_outline_24);
                stopUpdateSeekBarProgress();
            } else {
                mediaPlayer.start();
                pausePlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                startUpdateSeekBarProgress();
            }
            isPlaying = !isPlaying;
        }
    }

    private void startUpdateSeekBarProgress() {
        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    currentTimeTv.setText(formatTime(currentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateSeekBarRunnable);
    }

    private void stopUpdateSeekBarProgress() {
        if (updateSeekBarRunnable != null) {
            handler.removeCallbacks(updateSeekBarRunnable);
        }
    }

    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void updateFavoriteStatus() {
        Song currentSong = playlist.get(currentSongIndex);
        isFavorite = dbHelper.isSongFavorite(userId, currentSong.getId());
        updateFavoriteButton();
    }

    private void updateFavoriteButton() {
        favoriteButton.setImageResource(isFavorite ?
                R.drawable.ic_favorite : R.drawable.ic_favorite_border);
    }

    private void toggleFavorite() {
        Song currentSong = playlist.get(currentSongIndex);
        if (isFavorite) {
            dbHelper.removeFavoriteSong(userId, currentSong.getId());
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.addFavoriteSong(userId, currentSong.getId());
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
        }
        isFavorite = !isFavorite;
        updateFavoriteButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopUpdateSeekBarProgress();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}