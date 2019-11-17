package com.example.onlinelecturefairy.ui.grade;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.databinding.CommonGradeCardViewBinding;
import com.example.onlinelecturefairy.databinding.GradeCardViewBinding;
import com.example.onlinelecturefairy.grade.Grade;

import java.util.List;

public class GradeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Grade> grades;

    public GradeRecyclerAdapter(List<Grade> grades) {
        this.grades = grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades=  grades;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch(viewType) {
            case Grade.GRADE_TYPE_NORMAL:
            {
                GradeCardViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.grade_card_view, parent, false);
                return new GradeViewHolder(binding);
            }
            case Grade.GRADE_TYPE_COMMON:
            {
                CommonGradeCardViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.common_grade_card_view, parent, false);
                return new CommonGradeViewHolder(binding);
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Grade grade = grades.get(position);
        GradeViewModel model = new GradeViewModel();
        model.setGrade(grade);

        switch(grade.type) {
            case Grade.GRADE_TYPE_NORMAL:
            {
                GradeViewHolder holder = (GradeViewHolder) viewHolder;
                holder.setViewModel(model);
            }
            break;
            case Grade.GRADE_TYPE_COMMON:
            {
                CommonGradeViewHolder holder = (CommonGradeViewHolder) viewHolder;
                holder.setViewModel(model);
            }
            break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return grades.get(position).type;
    }

    @Override
    public int getItemCount() {
        return grades == null ? 0 : grades.size();
    }

    public class GradeViewHolder extends RecyclerView.ViewHolder {
        GradeCardViewBinding binding;

        public GradeViewHolder(@NonNull GradeCardViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setViewModel(GradeViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }
    }

    public class CommonGradeViewHolder extends  RecyclerView.ViewHolder {
        CommonGradeCardViewBinding binding;


        public CommonGradeViewHolder(@NonNull CommonGradeCardViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setViewModel(GradeViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }
    }
}
