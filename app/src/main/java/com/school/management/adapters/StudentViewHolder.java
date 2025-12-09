package com.school.management.adapters;

import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.school.management.R;
import android.view.View;

public class StudentViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivStatus;
    public TextView tvFullName;
    public TextView tvAdmissionNumber;
    public TextView tvClass;
    public TextView tvAdmissionDate;

    public StudentViewHolder(View itemView) {
        super(itemView);
        ivStatus = itemView.findViewById(R.id.ivStatus);
        tvFullName = itemView.findViewById(R.id.tvFullName);
        tvAdmissionNumber = itemView.findViewById(R.id.tvAdmissionNumber);
        tvClass = itemView.findViewById(R.id.tvClass);
        tvAdmissionDate = itemView.findViewById(R.id.tvAdmissionDate);
    }
}
