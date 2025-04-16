package com.example.placementhub.ui.placement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placementhub.R;
import com.example.placementhub.data.models.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StudentTableAdapter extends RecyclerView.Adapter<StudentTableAdapter.StudentViewHolder> {
    
    private List<Student> students = new ArrayList<>();
    private List<Student> selectedStudents = new ArrayList<>();
    private OnStudentItemClickListener listener;
    
    public interface OnStudentItemClickListener {
        void onStudentItemClick(Student student, int position);
        void onStudentSelectionChanged(List<Student> selectedStudents);
    }
    
    public StudentTableAdapter(OnStudentItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_table_row, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = students.get(position);
        holder.bind(student, position);
        
        // Set alternating row colors for better readability
        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.color.table_row_even);
        } else {
            holder.itemView.setBackgroundResource(R.color.table_row_odd);
        }
    }

    @Override
    public int getItemCount() {
        return students.size();
    }
    
    public void setStudents(List<Student> students) {
        this.students = students;
        notifyDataSetChanged();
    }
    
    public List<Student> getSelectedStudents() {
        return selectedStudents;
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private TextView nameTextView;
        private TextView branchTextView;
        private TextView rollNumberTextView;
        private TextView sapIdTextView;
        private TextView cgpaTextView;
        private TextView genderTextView;
        private TextView backlogsTextView;
        private TextView batchTextView;
        private TextView emailTextView;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.student_checkbox);
            nameTextView = itemView.findViewById(R.id.student_name);
            branchTextView = itemView.findViewById(R.id.student_branch);
            rollNumberTextView = itemView.findViewById(R.id.student_roll_number);
            sapIdTextView = itemView.findViewById(R.id.student_sap_id);
            cgpaTextView = itemView.findViewById(R.id.student_cgpa);
            genderTextView = itemView.findViewById(R.id.student_gender);
            backlogsTextView = itemView.findViewById(R.id.student_backlogs);
            batchTextView = itemView.findViewById(R.id.student_batch);
            emailTextView = itemView.findViewById(R.id.student_email);
        }

        public void bind(final Student student, final int position) {
            nameTextView.setText(student.getFullName());
            branchTextView.setText(student.getBranch());
            rollNumberTextView.setText(student.getRollNumber());
            sapIdTextView.setText(student.getSapId());
            cgpaTextView.setText(String.format("%.2f", student.getCgpa()));
            genderTextView.setText(student.getGender());
            backlogsTextView.setText(String.valueOf(student.getBacklogs()));
            batchTextView.setText(student.getBatch());
            emailTextView.setText(student.getEmail());
            
            // Set checkbox state
            boolean isSelected = selectedStudents.contains(student);
            checkBox.setChecked(isSelected);
            
            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStudentItemClick(student, position);
                }
            });
            
            checkBox.setOnClickListener(v -> {
                boolean isChecked = checkBox.isChecked();
                if (isChecked) {
                    if (!selectedStudents.contains(student)) {
                        selectedStudents.add(student);
                    }
                } else {
                    selectedStudents.remove(student);
                }
                
                if (listener != null) {
                    listener.onStudentSelectionChanged(selectedStudents);
                }
            });
        }
    }
    
    // Methods for bulk operations
    public void selectAll() {
        selectedStudents.clear();
        selectedStudents.addAll(students);
        notifyDataSetChanged();
        
        if (listener != null) {
            listener.onStudentSelectionChanged(selectedStudents);
        }
    }
    
    public void clearSelection() {
        selectedStudents.clear();
        notifyDataSetChanged();
        
        if (listener != null) {
            listener.onStudentSelectionChanged(selectedStudents);
        }
    }
    
    public void filterSelection(float minCgpa, int maxBacklogs) {
        selectedStudents = students.stream()
                .filter(s -> s.getCgpa() >= minCgpa && s.getBacklogs() <= maxBacklogs)
                .collect(Collectors.toList());
        notifyDataSetChanged();
        
        if (listener != null) {
            listener.onStudentSelectionChanged(selectedStudents);
        }
    }
} 