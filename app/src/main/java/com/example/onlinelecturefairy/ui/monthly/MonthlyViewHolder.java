package com.example.onlinelecturefairy.ui.monthly;

import android.view.View;

import com.example.onlinelecturefairy.databinding.FragmentDailyMonthlyBinding;
import com.example.onlinelecturefairy.ui.monthly.dailymonthly.DailyMonthlyViewModel;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class MonthlyViewHolder extends RecyclerView.ViewHolder {
    FragmentDailyMonthlyBinding binding;

    public MonthlyViewHolder(@NonNull FragmentDailyMonthlyBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void setViewModel(DailyMonthlyViewModel model) {
        binding.setModel(model);
        binding.executePendingBindings();
    }
}
