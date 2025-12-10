package com.school.management.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.management.R;
import com.school.management.models.StudentGrade;

import java.util.List;

public class GradebookAdapter extends RecyclerView.Adapter<GradebookAdapter.GradebookViewHolder> {
    private Context context;
    private List<StudentGrade> gradeList;
    private OnGradeEditListener onEditListener;

    public interface OnGradeEditListener {
        void onGradeEdit(StudentGrade grade);
    }

    public GradebookAdapter(Context context, List<StudentGrade> gradeList, OnGradeEditListener onEditListener) {
        this.context = context;
        this.gradeList = gradeList;
        this.onEditListener = onEditListener;
    }

    @NonNull
    @Override
    public GradebookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gradebook, parent, false);
        return new GradebookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradebookViewHolder holder, int position) {
        StudentGrade grade = gradeList.get(position);
        holder.bind(grade);
    }

    @Override
    public int getItemCount() {
        return gradeList.size();
    }

    public void updateList(List<StudentGrade> newList) {
        this.gradeList = newList;
        notifyDataSetChanged();
    }

    class GradebookViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStudentName, tvCalculatedGrade;
        private EditText etMarksObtained;

        GradebookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            etMarksObtained = itemView.findViewById(R.id.etMarksObtained);
            tvCalculatedGrade = itemView.findViewById(R.id.tvCalculatedGrade);
        }

        void bind(StudentGrade grade) {
            tvStudentName.setText(grade.getStudentName());
            etMarksObtained.setText(String.valueOf(grade.getMarksObtained()));
            
            // Update calculated grade
            String calculatedGrade = grade.calculateGrade(grade.getMarksObtained(), grade.getTotalMarks());
            tvCalculatedGrade.setText("Grade: " + calculatedGrade);

            // Listen for changes in marks
            etMarksObtained.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        int marks = Integer.parseInt(s.toString());
                        grade.setMarksObtained(marks);
                        String calculatedGrade = grade.calculateGrade(marks, grade.getTotalMarks());
                        tvCalculatedGrade.setText("Grade: " + calculatedGrade);
                        
                        // Notify listener to save
                        if (onEditListener != null) {
                            onEditListener.onGradeEdit(grade);
                        }
                    } catch (NumberFormatException e) {
                        // Handle invalid input
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }
}
