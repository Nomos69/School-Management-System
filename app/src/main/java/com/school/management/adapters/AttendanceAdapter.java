package com.school.management.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.management.R;
import com.school.management.models.Attendance;
import com.school.management.utils.DateTimeUtils;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {
    private Context context;
    private List<Attendance> attendanceList;
    private OnAttendanceClickListener onClickListener;

    public interface OnAttendanceClickListener {
        void onAttendanceClick(Attendance attendance);
    }

    public AttendanceAdapter(Context context, List<Attendance> attendanceList, OnAttendanceClickListener onClickListener) {
        this.context = context;
        this.attendanceList = attendanceList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attendance, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        Attendance attendance = attendanceList.get(position);
        holder.bind(attendance);
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public void updateList(List<Attendance> newList) {
        this.attendanceList = newList;
        notifyDataSetChanged();
    }

    class AttendanceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStudentInfo, tvDate, tvStatus, tvRemarks;

        AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentInfo = itemView.findViewById(R.id.tvStudentInfo);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRemarks = itemView.findViewById(R.id.tvRemarks);

            itemView.setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onAttendanceClick(attendanceList.get(getAdapterPosition()));
                }
            });
        }

        void bind(Attendance attendance) {
            tvStudentInfo.setText("Student ID: " + attendance.getStudentId());
            tvDate.setText("Date: " + DateTimeUtils.formatDate(attendance.getDate()));
            
            tvStatus.setText(attendance.getStatus());
            int statusColor = getStatusColor(attendance.getStatus());
            tvStatus.setTextColor(context.getResources().getColor(statusColor));
            
            String remarks = attendance.getRemarks() != null ? attendance.getRemarks() : "No remarks";
            tvRemarks.setText("Remarks: " + remarks);
        }

        private int getStatusColor(String status) {
            switch (status) {
                case "PRESENT":
                    return R.color.attendance_present;
                case "ABSENT":
                    return R.color.attendance_absent;
                case "LATE":
                    return R.color.attendance_late;
                case "EXCUSED":
                    return R.color.attendance_excused;
                default:
                    return R.color.text_secondary;
            }
        }
    }
}
