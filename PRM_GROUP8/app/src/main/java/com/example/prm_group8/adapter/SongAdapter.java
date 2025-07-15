package com.example.prm_group8.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_group8.R;
import com.example.prm_group8.model.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<Song> songs;
    private OnSongClickListener listener;

    public interface OnSongClickListener {
        void onSongClick(Song song);
        void onSongOptionsClick(Song song, View view); // Thêm callback cho menu
    }

    public SongAdapter(List<Song> songs, OnSongClickListener listener) {
        this.songs = songs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_song_item, parent, false); // Đổi layout thành item_song
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        if (songs == null || position >= songs.size()) return;

        Song currentSong = songs.get(position);

        // Set text
        holder.songNameTextView.setText(currentSong.getTitle());
        holder.artistNameTextView.setText(currentSong.getArtist());

        // Set image
        try {
            if (currentSong.getImage() != null && currentSong.getImage().length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(
                        currentSong.getImage(),
                        0,
                        currentSong.getImage().length
                );
                holder.songImageView.setImageBitmap(bitmap);
            } else {
                holder.songImageView.setImageResource(R.drawable.default_song_image);
            }
        } catch (Exception e) {
            holder.songImageView.setImageResource(R.drawable.default_song_image);
            e.printStackTrace();
        }

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(currentSong);
            }
        });

        // Options menu click
        holder.menuButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongOptionsClick(currentSong, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs != null ? songs.size() : 0;
    }

    public void updateList(List<Song> newList) {
        this.songs = newList;
        notifyDataSetChanged();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        public ImageView songImageView;
        public TextView songNameTextView;
        public TextView artistNameTextView;
        public ImageView menuButton;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songImageView = itemView.findViewById(R.id.songImage);
            songNameTextView = itemView.findViewById(R.id.songTitle);
            artistNameTextView = itemView.findViewById(R.id.artistName);
            menuButton = itemView.findViewById(R.id.menuButton);
        }
    }
}