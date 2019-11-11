package com.example.onlinelecturefairy.legacy.monthly.dailymonthly;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelecturefairy.databinding.FragmentEventBinding;
import com.example.onlinelecturefairy.legacy.event.EventViewModel;

public class DailyMonthlyViewHolder extends RecyclerView.ViewHolder {
    FragmentEventBinding binding;

    public DailyMonthlyViewHolder(@NonNull FragmentEventBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void setViewModel(EventViewModel model) {
        binding.setModel(model);
        binding.executePendingBindings();
    }
}
