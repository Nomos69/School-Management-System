package com.school.management.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.management.R;
import com.school.management.models.Examination;
import com.school.management.utils.DateTimeUtils;

import java.util.List;

public class ExaminationAdapter extends RecyclerView.Adapter<ExaminationAdapter.ExaminationViewHolder> {
    private Context context;
    private List<Examination> examinationList;
    private OnExaminationClickListener onClickListener;

    public interface OnExaminationClickListener {
        void onExaminationClick(Examination examination);
    }

    public ExaminationAdapter(Context context, List<Examination> examinationList, OnExaminationClickListener onClickListener) {
        this.context = context;
        this.examinationList = examinationList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ExaminationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_examination, parent, false);
        return new ExaminationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExaminationViewHolder holder, int position) {
        Examination examination = examinationList.get(position);
        holder.bind(examination);
    }

    @Override
    public int getItemCount() {
        return examinationList.size();
    }

    public void updateList(List<Examination> newList) {
        this.examinationList = newList;
        notifyDataSetChanged();
    }

    class ExaminationViewHolder extends RecyclerView.ViewHolder {
        private TextView tvExamName, tvSubject, tvExamDate, tvTotalMarks, tvExamStatus, tvClass;

        ExaminationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExamName = itemView.findViewById(R.id.tvExamName);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvExamDate = itemView.findViewById(R.id.tvExamDate);
            tvTotalMarks = itemView.findViewById(R.id.tvTotalMarks);
            tvExamStatus = itemView.findViewById(R.id.tvExamStatus);
            tvClass = itemView.findViewById(R.id.tvClass);

            itemView.setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onExaminationClick(examinationList.get(getAdapterPosition()));
                }
            });
        }

        void bind(Examination examination) {
            tvExamName.setText(examination.getExamName());
            tvSubject.setText("Subject: " + examination.getSubject());
            
            // Handle both Date and long formats for exam date
            String dateStr;
            long examTimeMillis;
            if (examination.getExamDate() != null) {
                dateStr = DateTimeUtils.formatDate(examination.getExamDate());
                examTimeMillis = examination.getExamDate().getTime();
            } else if (examination.getExamDateMillis() > 0) {
                dateStr = DateTimeUtils.formatDate(new java.util.Date(examination.getExamDateMillis()));
                examTimeMillis = examination.getExamDateMillis();
            } else {
                dateStr = "N/A";
                examTimeMillis = 0;
            }
            
            tvExamDate.setText("Date: " + dateStr);
            tvTotalMarks.setText("Total Marks: " + examination.getTotalMarks());
            tvClass.setText("Class: " + examination.getClassName());
            
            // Determine status based on exam date
            long currentTime = System.currentTimeMillis();
            String status;
            int statusColor;
            
            if (examTimeMillis > 0 && examTimeMillis < currentTime) {
                status = "Completed";
                statusColor = context.getResources().getColor(android.R.color.holo_green_dark);
            } else {
                status = "Upcoming";
                statusColor = context.getResources().getColor(android.R.color.holo_blue_dark);
            }
            
            tvExamStatus.setText(status);
            tvExamStatus.setTextColor(statusColor);
        }
    }
}
