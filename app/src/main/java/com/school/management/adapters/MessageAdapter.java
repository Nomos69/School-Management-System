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
import com.school.management.models.Message;
import com.school.management.utils.DateTimeUtils;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private Context context;
    private List<Message> messages;
    private OnMessageClickListener listener;

    public interface OnMessageClickListener {
        void onMessageClick(Message message);
    }

    public MessageAdapter(Context context, List<Message> messages, OnMessageClickListener listener) {
        this.context = context;
        this.messages = messages;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.tvSenderName.setText(message.getSenderId() != null ? message.getSenderId() : "Unknown");
        holder.tvSubject.setText(message.getSubject() != null ? message.getSubject() : "(No Subject)");
        holder.tvPreview.setText(message.getContent() != null ? 
                (message.getContent().length() > 50 ? message.getContent().substring(0, 50) + "..." : message.getContent()) 
                : "");

        if (message.getSentAt() != null) {
            holder.tvSentTime.setText(DateTimeUtils.formatDate(message.getSentAt()));
        }

        // Show read/unread status
        if (message.isRead()) {
            holder.ivUnread.setImageResource(R.drawable.ic_mail_read);
            holder.ivUnread.setAlpha(0.5f);
        } else {
            holder.ivUnread.setImageResource(R.drawable.ic_mail_unread);
            holder.ivUnread.setAlpha(1.0f);
        }

        // Show priority badge
        int priorityColor = getPriorityColor(message.getPriority());
        holder.ivPriority.setColorFilter(context.getResources().getColor(priorityColor));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMessageClick(message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
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

    public void updateList(List<Message> newList) {
        messages.clear();
        messages.addAll(newList);
        notifyDataSetChanged();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvSenderName, tvSubject, tvPreview, tvSentTime;
        ImageView ivUnread, ivPriority;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvPreview = itemView.findViewById(R.id.tvPreview);
            tvSentTime = itemView.findViewById(R.id.tvSentTime);
            ivUnread = itemView.findViewById(R.id.ivUnread);
            ivPriority = itemView.findViewById(R.id.ivPriority);
        }
    }
}
