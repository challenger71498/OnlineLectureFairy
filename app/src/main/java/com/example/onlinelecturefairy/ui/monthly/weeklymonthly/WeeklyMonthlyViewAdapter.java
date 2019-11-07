package com.example.onlinelecturefairy.ui.monthly.weeklymonthly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.common.BindingRecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;

public class WeeklyMonthlyViewAdapter extends BindingRecyclerViewAdapter<Calendar, WeeklyMonthlyViewHolder> {
    public WeeklyMonthlyViewAdapter(Context context) {
        super(context);
    }

    public WeeklyMonthlyViewAdapter(Context context, List<Calendar> arrayList) {
        super(context, arrayList);
    }

    @Override
    public void onBindView(WeeklyMonthlyViewHolder holder, int position) {
        Calendar date = getItem(position);
        holder.binding.setDate(new SimpleDateFormat("d", Locale.KOREAN).format(date));
    }

    @NonNull
    @Override
    public WeeklyMonthlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_event, parent, false);
        return new WeeklyMonthlyViewHolder(view);
    }
}
