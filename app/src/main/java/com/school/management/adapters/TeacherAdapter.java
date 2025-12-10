package com.school.management.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.school.management.R;
import com.school.management.models.Teacher;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {
    private Context context;
    private List<Teacher> teachers;
    private OnTeacherClickListener listener;
    private OnTeacherLongClickListener longClickListener;

    public interface OnTeacherClickListener {
        void onTeacherClick(Teacher teacher);
    }

    public interface OnTeacherLongClickListener {
        void onTeacherLongClick(Teacher teacher);
    }

    public TeacherAdapter(Context context, List<Teacher> teachers, OnTeacherClickListener listener) {
        this.context = context;
        this.teachers = teachers;
        this.listener = listener;
    }

    public void setOnTeacherLongClickListener(OnTeacherLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TeacherViewHolder(LayoutInflater.from(context).inflate(R.layout.item_teacher, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        Teacher teacher = teachers.get(position);
        
        holder.tvFullName.setText(teacher.getFirstName() + " " + teacher.getLastName());
        holder.tvEmployeeId.setText("EmpID: " + (teacher.getEmployeeId() != null ? teacher.getEmployeeId() : "N/A"));
        holder.tvDesignation.setText("Designation: " + (teacher.getDesignation() != null ? teacher.getDesignation() : "N/A"));
        
        if (teacher.isActive()) {
            holder.ivStatus.setImageResource(R.drawable.ic_check_circle);
        } else {
            holder.ivStatus.setImageResource(R.drawable.ic_cancel);
        }
        
        if (teacher.getJoiningDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            holder.tvJoiningDate.setText("Joined: " + sdf.format(teacher.getJoiningDate()));
        }
        
        holder.itemView.setOnClickListener(v -> listener.onTeacherClick(teacher));
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onTeacherLongClick(teacher);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public void updateList(List<Teacher> newList) {
        teachers.clear();
        teachers.addAll(newList);
        notifyDataSetChanged();
    }

    public static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName, tvEmployeeId, tvDesignation, tvJoiningDate;
        ImageView ivStatus;

        public TeacherViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvEmployeeId = itemView.findViewById(R.id.tvEmployeeId);
            tvDesignation = itemView.findViewById(R.id.tvDesignation);
            tvJoiningDate = itemView.findViewById(R.id.tvJoiningDate);
            ivStatus = itemView.findViewById(R.id.ivStatus);
        }
    }
}
