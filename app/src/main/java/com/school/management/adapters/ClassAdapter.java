package com.school.management.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.management.R;
import com.school.management.models.SchoolClass;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    private Context context;
    private List<SchoolClass> classList;
    private OnClassClickListener onClickListener;
    private OnClassLongClickListener onLongClickListener;

    public interface OnClassClickListener {
        void onClassClick(SchoolClass schoolClass);
    }

    public interface OnClassLongClickListener {
        void onClassLongClick(SchoolClass schoolClass);
    }

    public ClassAdapter(Context context, List<SchoolClass> classList, OnClassClickListener onClickListener) {
        this.context = context;
        this.classList = classList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        SchoolClass schoolClass = classList.get(position);
        holder.bind(schoolClass);
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public void setOnClassLongClickListener(OnClassLongClickListener listener) {
        this.onLongClickListener = listener;
    }

    public void updateList(List<SchoolClass> newList) {
        this.classList = newList;
        notifyDataSetChanged();
    }

    class ClassViewHolder extends RecyclerView.ViewHolder {
        private TextView tvClassName, tvSection, tvStudentCount, tvStatus;

        ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvSection = itemView.findViewById(R.id.tvSection);
            tvStudentCount = itemView.findViewById(R.id.tvStudentCount);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            itemView.setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onClassClick(classList.get(getAdapterPosition()));
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (onLongClickListener != null) {
                    onLongClickListener.onClassLongClick(classList.get(getAdapterPosition()));
                }
                return true;
            });
        }

        void bind(SchoolClass schoolClass) {
            tvClassName.setText(schoolClass.getClassName());
            tvSection.setText("Section: " + schoolClass.getSection());
            
            int studentCount = schoolClass.getStudentIds() != null ? schoolClass.getStudentIds().size() : 0;
            tvStudentCount.setText("Students: " + studentCount + "/" + schoolClass.getMaxStudents());
            
            tvStatus.setText(schoolClass.isActive() ? "Active" : "Inactive");
            tvStatus.setTextColor(schoolClass.isActive() ? 
                context.getResources().getColor(R.color.success) : 
                context.getResources().getColor(R.color.error));
        }
    }
}
