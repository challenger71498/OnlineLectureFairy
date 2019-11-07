package com.example.onlinelecturefairy.ui.monthly.dailymonthly;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelecturefairy.databinding.FragmentEventBinding;

public class DailyMonthlyViewHolder extends RecyclerView.ViewHolder {
    FragmentEventBinding binding;

    public DailyMonthlyViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }
}
