package com.example.onlinelecturefairy.ui.monthly;

import com.example.onlinelecturefairy.databinding.DailyMonthlyBinding;
import com.example.onlinelecturefairy.ui.monthly.dailymonthly.DailyMonthlyViewModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MonthlyViewHolder extends RecyclerView.ViewHolder {
    DailyMonthlyBinding binding;

    public MonthlyViewHolder(@NonNull DailyMonthlyBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void setViewModel(DailyMonthlyViewModel model) {
        binding.setModel(model);
        binding.executePendingBindings();
    }
}
