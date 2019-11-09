package com.example.onlinelecturefairy.ui.monthly.dailymonthly;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelecturefairy.databinding.FragmentEventBinding;
import com.example.onlinelecturefairy.ui.event.EventViewModel;

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
