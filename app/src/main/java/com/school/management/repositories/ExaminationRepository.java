package com.school.management.repositories;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.school.management.models.Examination;
import com.school.management.models.StudentGrade;

import java.util.ArrayList;
import java.util.List;

public class ExaminationRepository {
    private FirebaseFirestore firestore;

    public ExaminationRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Add or update an examination
     */
    public void saveExamination(Examination examination, OnCompleteListener listener) {
        String examId = examination.getExaminationId() != null ? examination.getExaminationId() : examination.getExamId();
        
        firestore.collection("examinations")
                .document(examId)
                .set(examination.toMap())
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) {
                        listener.onSuccess("Examination saved successfully");
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onFailure(e.getMessage());
                    }
                });
    }

    /**
     * Get examination by ID
     */
    public void getExamination(String examId, OnExaminationLoadListener listener) {
        firestore.collection("examinations")
                .document(examId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (listener != null) {
                            listener.onError(error.getMessage());
                        }
                        return;
                    }

                    if (value != null && value.exists()) {
                        Examination examination = value.toObject(Examination.class);
                        if (listener != null) {
                            listener.onExaminationLoaded(examination);
                        }
                    }
                });
    }

    /**
     * Get all examinations for a class
     */
    public void getExaminationsByClass(String classId, OnExaminationListListener listener) {
        firestore.collection("examinations")
                .whereEqualTo("classId", classId)
                .orderBy("examDate", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (listener != null) {
                            listener.onError(error.getMessage());
                        }
                        return;
                    }

                    List<Examination> exams = new ArrayList<>();
                    if (value != null) {
                        exams.addAll(value.toObjects(Examination.class));
                    }
                    if (listener != null) {
                        listener.onExaminationListLoaded(exams);
                    }
                });
    }

    /**
     * Get all examinations
     */
    public void getAllExaminations(OnExaminationListListener listener) {
        firestore.collection("examinations")
                .orderBy("examDate", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (listener != null) {
                            listener.onError(error.getMessage());
                        }
                        return;
                    }

                    List<Examination> exams = new ArrayList<>();
                    if (value != null) {
                        exams.addAll(value.toObjects(Examination.class));
                    }
                    if (listener != null) {
                        listener.onExaminationListLoaded(exams);
                    }
                });
    }

    /**
     * Delete examination
     */
    public void deleteExamination(String examId, OnCompleteListener listener) {
        firestore.collection("examinations")
                .document(examId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) {
                        listener.onSuccess("Examination deleted successfully");
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onFailure(e.getMessage());
                    }
                });
    }

    /**
     * Save student grade
     */
    public void saveStudentGrade(StudentGrade grade, OnCompleteListener listener) {
        String gradeId = grade.getStudentId() + "_" + grade.getExamId();
        
        firestore.collection("studentGrades")
                .document(gradeId)
                .set(grade.toMap())
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) {
                        listener.onSuccess("Grade saved successfully");
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onFailure(e.getMessage());
                    }
                });
    }

    /**
     * Get grades for an exam and class
     */
    public void getGradesForExam(String examId, String classId, OnGradeListListener listener) {
        firestore.collection("studentGrades")
                .whereEqualTo("examId", examId)
                .whereEqualTo("classId", classId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (listener != null) {
                            listener.onError(error.getMessage());
                        }
                        return;
                    }

                    List<StudentGrade> grades = new ArrayList<>();
                    if (value != null) {
                        grades.addAll(value.toObjects(StudentGrade.class));
                    }
                    if (listener != null) {
                        listener.onGradeListLoaded(grades);
                    }
                });
    }

    /**
     * Get student's report card
     */
    public void getStudentReportCard(String studentId, OnGradeListListener listener) {
        firestore.collection("studentGrades")
                .whereEqualTo("studentId", studentId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (listener != null) {
                            listener.onError(error.getMessage());
                        }
                        return;
                    }

                    List<StudentGrade> grades = new ArrayList<>();
                    if (value != null) {
                        grades.addAll(value.toObjects(StudentGrade.class));
                    }
                    if (listener != null) {
                        listener.onGradeListLoaded(grades);
                    }
                });
    }

    // Listener interfaces
    public interface OnCompleteListener {
        void onSuccess(String message);
        void onFailure(String error);
    }

    public interface OnExaminationLoadListener {
        void onExaminationLoaded(Examination examination);
        void onError(String error);
    }

    public interface OnExaminationListListener {
        void onExaminationListLoaded(List<Examination> examinations);
        void onError(String error);
    }

    public interface OnGradeListListener {
        void onGradeListLoaded(List<StudentGrade> grades);
        void onError(String error);
    }
}
