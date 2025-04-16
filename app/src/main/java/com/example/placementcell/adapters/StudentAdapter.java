package com.example.placementcell.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placementhub.R;
import com.example.placementhub.data.models.Student;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private OnStudentItemClickListener listener;

    public interface OnStudentItemClickListener {
        void onStudentItemClick(Student student, int position);
        void onStudentCheckboxClick(Student student, int position, boolean isChecked);
    }

    public StudentAdapter(List<Student> studentList, OnStudentItemClickListener listener) {
        this.studentList = studentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_row, parent, false);
        return new StudentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        
        holder.checkboxSelect.setChecked(student.isSelected());
        holder.textName.setText(student.getFullName());
        holder.textBranch.setText(student.getBranch());
        holder.textRollNumber.setText(student.getRollNumber());
        holder.textSapId.setText(student.getSapId());
        holder.textCgpa.setText(String.valueOf(student.getCgpa()));
        holder.textGender.setText(student.getGender());
        holder.textBacklogs.setText(String.valueOf(student.getBacklogs()));
        holder.textBatch.setText(student.getBatch());
        holder.textEmail.setText(student.getEmail());
        
        holder.bindListeners(student, position);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public void updateStudentList(List<Student> newStudentList) {
        this.studentList = newStudentList;
        notifyDataSetChanged();
    }

    public List<Student> getSelectedStudents() {
        return studentList.stream()
                .filter(Student::isSelected)
                .collect(java.util.stream.Collectors.toList());
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkboxSelect;
        TextView textName, textBranch, textRollNumber, textSapId, 
                 textCgpa, textGender, textBacklogs, textBatch, textEmail;

        StudentViewHolder(View view) {
            super(view);
            checkboxSelect = view.findViewById(R.id.checkbox_select);
            textName = view.findViewById(R.id.text_name);
            textBranch = view.findViewById(R.id.text_branch);
            textRollNumber = view.findViewById(R.id.text_roll_number);
            textSapId = view.findViewById(R.id.text_sap_id);
            textCgpa = view.findViewById(R.id.text_cgpa);
            textGender = view.findViewById(R.id.text_gender);
            textBacklogs = view.findViewById(R.id.text_backlogs);
            textBatch = view.findViewById(R.id.text_batch);
            textEmail = view.findViewById(R.id.text_email);
        }

        void bindListeners(final Student student, final int position) {
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStudentItemClick(student, position);
                }
            });

            checkboxSelect.setOnClickListener(v -> {
                boolean isChecked = checkboxSelect.isChecked();
                student.setSelected(isChecked);
                if (listener != null) {
                    listener.onStudentCheckboxClick(student, position, isChecked);
                }
            });
        }
    }
} 