package com.school.management.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.management.R;
import com.school.management.models.User;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<User> userList;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public UserAdapter(Context context, List<User> userList, OnUserClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        
        holder.tvFullName.setText(user.getFullName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvRole.setText(user.getRole());
        
        // Set role badge color
        int badgeColor = getRoleBadgeColor(user.getRole());
        holder.tvRole.setBackgroundResource(badgeColor);
        
        // Show active status
        if (user.isActive()) {
            holder.ivStatus.setImageResource(R.drawable.ic_check_circle);
            holder.ivStatus.setColorFilter(context.getResources().getColor(R.color.success));
        } else {
            holder.ivStatus.setImageResource(R.drawable.ic_cancel);
            holder.ivStatus.setColorFilter(context.getResources().getColor(R.color.error));
        }
        
        // Format date
        if (user.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            holder.tvCreatedAt.setText("Joined: " + sdf.format(user.getCreatedAt()));
        }
        
        holder.itemView.setOnClickListener(v -> listener.onUserClick(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private int getRoleBadgeColor(String role) {
        switch (role) {
            case "ADMIN":
                return R.drawable.badge_admin;
            case "TEACHER":
                return R.drawable.badge_teacher;
            case "STUDENT":
                return R.drawable.badge_student;
            case "PARENT":
                return R.drawable.badge_parent;
            case "STAFF":
                return R.drawable.badge_staff;
            default:
                return R.drawable.badge_default;
        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName, tvEmail, tvRole, tvCreatedAt;
        ImageView ivStatus;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            ivStatus = itemView.findViewById(R.id.ivStatus);
        }
    }
}
