package com.example.prm_group8.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_group8.R;
import com.example.prm_group8.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> users;
    private OnUserDeleteListener deleteListener;

    public interface OnUserDeleteListener {
        void onUserDelete(User user);
    }

    public UserAdapter(List<User> users, OnUserDeleteListener deleteListener) {
        this.users = users;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onUserDelete(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarImageView;
        private TextView usernameTextView;
        private TextView emailTextView;
        private TextView roleTextView;
        private Button deleteButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            roleTextView = itemView.findViewById(R.id.roleTextView);
            deleteButton = itemView.findViewById(R.id.btn_detail);
        }

        public void bind(User user) {
            Log.d("UserViewHolder", "Binding user: " + user.toString());

            // Xử lý avatar
            if (user.getImage() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(user.getImage(), 0, user.getImage().length);
                avatarImageView.setImageBitmap(bitmap);
            } else {
                // Đặt ảnh mặc định nếu không có avatar
                avatarImageView.setImageResource(R.drawable.default_avatar);
            }

            usernameTextView.setText(user.getUsername());
            emailTextView.setText(user.getEmail());
            roleTextView.setText(user.getRole());
        }
    }
}