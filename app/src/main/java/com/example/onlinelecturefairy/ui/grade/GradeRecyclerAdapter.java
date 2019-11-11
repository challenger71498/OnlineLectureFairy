package com.example.onlinelecturefairy.ui.grade;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.databinding.GradeCardViewBinding;
import com.example.onlinelecturefairy.grade.Grade;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class GradeRecyclerAdapter extends RecyclerView.Adapter<GradeRecyclerAdapter.ViewHolder> {
    List<Grade> grades;

    public GradeRecyclerAdapter(List<Grade> grades) {
        this.grades = grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades=  grades;
    }

    @NonNull
    @Override
    public GradeRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GradeCardViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.grade_card_view, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeRecyclerAdapter.ViewHolder viewHolder, int position) {
        Grade grade = grades.get(position);
        GradeViewModel model = new GradeViewModel();
        model.setGrade(grade);
        viewHolder.setViewModel(model);
    }

    @Override
    public int getItemCount() {
        return grades == null ? 0 : grades.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        GradeCardViewBinding binding;

        public ViewHolder(@NonNull GradeCardViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setViewModel(GradeViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }
    }
}
