package com.example.prm_group8.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_group8.R;
import com.example.prm_group8.controller.Play_song;
import com.example.prm_group8.model.Song;

import java.util.List;

public class ListeningHistoryAdapter extends RecyclerView.Adapter<ListeningHistoryAdapter.ListeningHistoryViewHolder> {

    private final List<Song> songList;
    private final Context context;
    private int userId;

    public ListeningHistoryAdapter(List<Song> songList, Context context, int userId) {
        if (songList == null) {
            throw new IllegalArgumentException("songList cannot be null");
        }
        this.songList = songList;
        this.context = context;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ListeningHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listening_history, parent, false);
        return new ListeningHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListeningHistoryViewHolder holder, int position) {
        Song song = songList.get(position);

        // Kiểm tra song không phải null trước khi gán giá trị
        if (song != null) {
            holder.titleTextView.setText(song.getTitle());
            holder.artistTextView.setText(song.getArtist());
            // Chuyển đổi byte[] thành Bitmap và hiển thị
            if (song.getImage() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(song.getImage(), 0, song.getImage().length);
                holder.imageView.setImageBitmap(bitmap);
            } else {
                // Đặt một hình ảnh mặc định nếu không có ảnh
                holder.imageView.setImageResource(R.drawable.default_song_image);
            }
        }

        holder.btnDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, Play_song.class);
            intent.putExtra("position", song.getId() - 1);
            intent.putExtra("USER_ID", userId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    static class ListeningHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView artistTextView;
        Button btnDetail;
        ImageView imageView;

        ListeningHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.song_title);
            artistTextView = itemView.findViewById(R.id.artist_name);
            btnDetail = itemView.findViewById(R.id.btn_detail); // Đảm bảo bạn có button trong layout
            imageView = itemView.findViewById(R.id.img_song);
        }
    }
}
