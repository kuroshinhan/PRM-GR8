
package com.example.prm_group8;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_group8.model.Song;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private List<Song> songs; // Thay đổi từ PlaylistItem thành Song
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position, List<Song> playlist);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PlaylistAdapter(List<Song> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Song currentSong = songs.get(position);
        holder.bind(currentSong, position);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private final ImageView playlistImage;
        private final TextView songNameText;
        private final TextView artistNameText;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistImage = itemView.findViewById(R.id.playlist_image);
            songNameText = itemView.findViewById(R.id.song_name);
            artistNameText = itemView.findViewById(R.id.artist_name);
        }

        public void bind(Song song, int position) {
            // Set text
            songNameText.setText(song.getTitle());
            artistNameText.setText(song.getArtist());

            // Set image
            if (song.getImage() != null && song.getImage().length > 0) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(
                            song.getImage(),
                            0,
                            song.getImage().length
                    );
                    playlistImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    playlistImage.setImageResource(R.drawable.img);
                }
            } else {
                playlistImage.setImageResource(R.drawable.img);
            }

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    List<Song> playlist = new ArrayList<>(songs); // Sử dụng danh sách songs trực tiếp
                    listener.onItemClick(position, playlist);
                }
            });
        }
    }

    // Helper methods
    public void updatePlaylist(List<Song> newSongs) {
        this.songs = newSongs;
        notifyDataSetChanged();
    }

    public void addItem(Song song) {
        this.songs.add(song);
        notifyItemInserted(songs.size() - 1);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < songs.size()) {
            songs.remove(position);
            notifyItemRemoved(position);
        }
    }
}
