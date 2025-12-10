package com.school.management.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.management.R;
import com.school.management.models.Event;
import com.school.management.utils.DateTimeUtils;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private Context context;
    private List<Event> eventList;
    private OnEventClickListener onClickListener;

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public EventAdapter(Context context, List<Event> eventList, OnEventClickListener onClickListener) {
        this.context = context;
        this.eventList = eventList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateList(List<Event> newList) {
        this.eventList = newList;
        notifyDataSetChanged();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEventName, tvEventType, tvStartDate, tvLocation, tvParticipantCount, tvOrganizer, tvRegistrationStatus;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventType = itemView.findViewById(R.id.tvEventType);
            tvStartDate = itemView.findViewById(R.id.tvStartDate);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvParticipantCount = itemView.findViewById(R.id.tvParticipantCount);
            tvOrganizer = itemView.findViewById(R.id.tvOrganizer);
            tvRegistrationStatus = itemView.findViewById(R.id.tvRegistrationStatus);

            itemView.setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onEventClick(eventList.get(getAdapterPosition()));
                }
            });
        }

        void bind(Event event) {
            tvEventName.setText(event.getEventName());
            tvEventType.setText(event.getEventType());
            tvStartDate.setText(DateTimeUtils.formatDate(event.getStartDate()));
            tvLocation.setText(event.getLocation());
            tvOrganizer.setText(event.getOrganizer());
            
            int participantCount = event.getParticipantIds() != null ? event.getParticipantIds().size() : 0;
            int maxParticipants = event.getMaxParticipants() != 0 ? event.getMaxParticipants() : 100;
            tvParticipantCount.setText(participantCount + "/" + maxParticipants);
            
            boolean isOpen = participantCount < maxParticipants;
            tvRegistrationStatus.setText(isOpen ? "Open" : "Closed");
            tvRegistrationStatus.setTextColor(itemView.getContext().getColor(isOpen ? android.R.color.holo_green_dark : android.R.color.holo_red_dark));
        }
    }
}
