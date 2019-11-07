package com.example.onlinelecturefairy.ui.monthly.weeklymonthly;

import android.view.View;

import com.example.onlinelecturefairy.databinding.FragmentDailyMonthlyBinding;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class WeeklyMonthlyViewHolder extends RecyclerView.ViewHolder {
    FragmentDailyMonthlyBinding binding;

    public WeeklyMonthlyViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }
}
