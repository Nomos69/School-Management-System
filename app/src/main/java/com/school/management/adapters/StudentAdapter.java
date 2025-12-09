package com.school.management.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.school.management.R;
import com.school.management.models.Student;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class StudentAdapter extends RecyclerView.Adapter<StudentViewHolder> {
    private Context context;
    private List<Student> students;
    private OnStudentClickListener listener;
    private OnStudentLongClickListener longClickListener;

    public interface OnStudentClickListener {
        void onStudentClick(Student student);
    }

    public interface OnStudentLongClickListener {
        void onStudentLongClick(Student student);
    }

    public StudentAdapter(Context context, List<Student> students, OnStudentClickListener listener) {
        this.context = context;
        this.students = students;
        this.listener = listener;
    }

    public void setOnStudentLongClickListener(OnStudentLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StudentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_student, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = students.get(position);
        
        holder.tvFullName.setText(student.getFirstName() + " " + student.getLastName());
        holder.tvAdmissionNumber.setText("Adm: " + (student.getAdmissionNumber() != null ? student.getAdmissionNumber() : "N/A"));
        holder.tvClass.setText("Class: " + (student.getClassId() != null ? student.getClassId() : "N/A"));
        
        if (student.isActive()) {
            holder.ivStatus.setImageResource(R.drawable.ic_check_circle);
        } else {
            holder.ivStatus.setImageResource(R.drawable.ic_cancel);
        }
        
        if (student.getAdmissionDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            holder.tvAdmissionDate.setText("Enrolled: " + sdf.format(student.getAdmissionDate()));
        }
        
        holder.itemView.setOnClickListener(v -> listener.onStudentClick(student));
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onStudentLongClick(student);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public void updateList(List<Student> newList) {
        students.clear();
        students.addAll(newList);
        notifyDataSetChanged();
    }
}
