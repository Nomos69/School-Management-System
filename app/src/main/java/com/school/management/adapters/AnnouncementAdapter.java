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
import com.school.management.models.Announcement;
import com.school.management.utils.DateTimeUtils;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {
    private Context context;
    private List<Announcement> announcements;
    private OnAnnouncementClickListener listener;

    public interface OnAnnouncementClickListener {
        void onAnnouncementClick(Announcement announcement);
    }

    public AnnouncementAdapter(Context context, List<Announcement> announcements, OnAnnouncementClickListener listener) {
        this.context = context;
        this.announcements = announcements;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_announcement, parent, false);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        Announcement announcement = announcements.get(position);

        holder.tvTitle.setText(announcement.getTitle() != null ? announcement.getTitle() : "No Title");
        holder.tvDescription.setText(announcement.getDescription() != null ? 
                (announcement.getDescription().length() > 100 ? 
                        announcement.getDescription().substring(0, 100) + "..." : 
                        announcement.getDescription()) 
                : "");

        if (announcement.getPostedDate() != null) {
            holder.tvPostedDate.setText(DateTimeUtils.formatDate(announcement.getPostedDate()));
        }

        holder.tvCategory.setText(announcement.getCategory() != null ? announcement.getCategory() : "NOTICE");

        // Set category badge color
        int categoryColor = getCategoryColor(announcement.getCategory());
        holder.ivCategoryBadge.setColorFilter(context.getResources().getColor(categoryColor));

        // Set priority indicator
        int priorityColor = getPriorityColor(announcement.getPriority());
        holder.ivPriorityBadge.setColorFilter(context.getResources().getColor(priorityColor));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAnnouncementClick(announcement);
            }
        });
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    private int getCategoryColor(String category) {
        switch (category != null ? category : "NOTICE") {
            case "ACADEMIC":
                return R.color.category_academic;
            case "EVENT":
                return R.color.category_event;
            case "EMERGENCY":
                return R.color.category_emergency;
            case "NOTICE":
            default:
                return R.color.category_notice;
        }
    }

    private int getPriorityColor(String priority) {
        switch (priority != null ? priority : "MEDIUM") {
            case "URGENT":
                return R.color.priority_urgent;
            case "HIGH":
                return R.color.priority_high;
            case "LOW":
                return R.color.priority_low;
            case "MEDIUM":
            default:
                return R.color.priority_medium;
        }
    }

    public void updateList(List<Announcement> newList) {
        announcements.clear();
        announcements.addAll(newList);
        notifyDataSetChanged();
    }

    static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvPostedDate, tvCategory;
        ImageView ivCategoryBadge, ivPriorityBadge;

        AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPostedDate = itemView.findViewById(R.id.tvPostedDate);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            ivCategoryBadge = itemView.findViewById(R.id.ivCategoryBadge);
            ivPriorityBadge = itemView.findViewById(R.id.ivPriorityBadge);
        }
    }
}
