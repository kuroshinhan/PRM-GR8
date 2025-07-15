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

import com.example.prm_group8.database.DBHelper;
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
    private int currentSongIndex = -1;
    private int userId = -1;
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
        favoriteButton = findViewById(R.id.favoriteButton);

        if (pausePlay != null) {
            pausePlay.setImageResource(R.drawable.baseline_play_circle_outline_24);
        }
    }

    private void setupClickListeners() {
        if (btnBack != null) btnBack.setOnClickListener(v -> onBackPressed());
        if (nextBtn != null) nextBtn.setOnClickListener(v -> playNextSong());
        if (previousBtn != null) previousBtn.setOnClickListener(v -> playPreviousSong());
        if (pausePlay != null) pausePlay.setOnClickListener(v -> togglePlayPause());
        if (favoriteButton != null) favoriteButton.setOnClickListener(v -> toggleFavorite());
    }

    private void getIntentData() {
        currentSongIndex = getIntent().getIntExtra("position", -1);
        userId = getIntent().getIntExtra("USER_ID", -1);

        if (currentSongIndex < 0 || userId < 0) {
            Log.e(TAG, "Invalid song position or user ID: position=" + currentSongIndex + ", userId=" + userId);
            Toast.makeText(this, "Error: Invalid data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPlaylist();
    }

    private void playNextSong() {
        if (playlist == null || playlist.isEmpty()) return;
        currentSongIndex = (currentSongIndex + 1) % playlist.size();
        playSong();
    }

    private void playPreviousSong() {
        if (playlist == null || playlist.isEmpty()) return;
        currentSongIndex = (currentSongIndex - 1 + playlist.size()) % playlist.size();
        playSong();
    }

    private void loadPlaylist() {
        playlist = dbHelper.getAllSongs();
        if (playlist == null || playlist.isEmpty() || currentSongIndex >= playlist.size()) {
            Log.e(TAG, "Invalid playlist data or index: size=" + (playlist != null ? playlist.size() : 0) + ", index=" + currentSongIndex);
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
            mediaPlayer = null;
        }

        if (playlist == null || currentSongIndex < 0 || currentSongIndex >= playlist.size()) {
            Log.e(TAG, "Invalid song index: " + currentSongIndex);
            return;
        }

        Song song = playlist.get(currentSongIndex);
        if (song == null) {
            Log.e(TAG, "Song at index " + currentSongIndex + " is null");
            return;
        }

        updateSongInfo(song);
        setupMediaPlayer(song);
        updateFavoriteStatus();
    }

    private void updateSongInfo(Song song) {
        runOnUiThread(() -> {
            if (titleTv != null) titleTv.setText(song.getTitle());
            if (artistTv != null) artistTv.setText(song.getArtist());
            updateMusicIcon(song);
        });
    }

    private void updateMusicIcon(Song song) {
        runOnUiThread(() -> {
            if (musicIcon != null) {
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
        });
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
                if (totalTimeTv != null) totalTimeTv.setText(formatTime(mediaPlayer.getDuration()));
                if (seekBar != null) seekBar.setMax(mediaPlayer.getDuration());
                startPlayback();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
                runOnUiThread(() -> Toast.makeText(Play_song.this, "Error playing audio", Toast.LENGTH_SHORT).show());
                return false;
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                runOnUiThread(() -> {
                    if (pausePlay != null) pausePlay.setImageResource(R.drawable.baseline_play_circle_outline_24);
                    stopUpdateSeekBarProgress();
                    isPlaying = false;
                    playNextSong();
                });
            });

        } catch (IOException e) {
            Log.e(TAG, "Error setting data source for song: " + song.getTitle(), e);
            runOnUiThread(() -> Toast.makeText(this, "Error playing song", Toast.LENGTH_SHORT).show());
        }

        setupSeekBar();
    }

    private void startPlayback() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isPlaying = true;
            runOnUiThread(() -> {
                if (pausePlay != null) pausePlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                startUpdateSeekBarProgress();
            });
        }
    }

    private void setupSeekBar() {
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && mediaPlayer != null) {
                        mediaPlayer.seekTo(progress);
                        if (currentTimeTv != null) currentTimeTv.setText(formatTime(progress));
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
    }

    private void togglePlayPause() {
        if (mediaPlayer != null) {
            if (isPlaying) {
                mediaPlayer.pause();
                runOnUiThread(() -> {
                    if (pausePlay != null) pausePlay.setImageResource(R.drawable.baseline_play_circle_outline_24);
                    stopUpdateSeekBarProgress();
                });
            } else {
                mediaPlayer.start();
                runOnUiThread(() -> {
                    if (pausePlay != null) pausePlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                    startUpdateSeekBarProgress();
                });
            }
            isPlaying = !isPlaying;
        }
    }

    private void startUpdateSeekBarProgress() {
        updateSeekBarRunnable = () -> {
            if (mediaPlayer != null && seekBar != null && currentTimeTv != null) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                currentTimeTv.setText(formatTime(currentPosition));
            }
            handler.postDelayed(updateSeekBarRunnable, 1000);
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
        if (playlist != null && currentSongIndex >= 0 && currentSongIndex < playlist.size()) {
            Song currentSong = playlist.get(currentSongIndex);
            isFavorite = dbHelper.isSongFavorite(userId, currentSong.getId());
            runOnUiThread(this::updateFavoriteButton);
        }
    }

    private void updateFavoriteButton() {
        if (favoriteButton != null) {
            favoriteButton.setImageResource(isFavorite ?
                    R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        }
    }

    private void toggleFavorite() {
        if (playlist != null && currentSongIndex >= 0 && currentSongIndex < playlist.size()) {
            Song currentSong = playlist.get(currentSongIndex);
            if (isFavorite) {
                if (dbHelper.removeFavoriteSong(userId, currentSong.getId())) {
                    Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (dbHelper.addFavoriteSong(userId, currentSong.getId())) {
                    Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
                }
            }
            isFavorite = !isFavorite;
            updateFavoriteButton();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopUpdateSeekBarProgress();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}