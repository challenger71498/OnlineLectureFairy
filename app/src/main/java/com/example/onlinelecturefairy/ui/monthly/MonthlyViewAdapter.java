package com.example.onlinelecturefairy.ui.monthly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.databinding.DailyMonthlyBinding;
import com.example.onlinelecturefairy.day.Day;
import com.example.onlinelecturefairy.ui.monthly.dailymonthly.DailyMonthlyViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class MonthlyViewAdapter extends RecyclerView.Adapter {
    private ArrayList<Day> convert(TreeMap<Calendar, Day> treeMap) {
        ArrayList<Day> list = new ArrayList<>();
        for(Calendar key : treeMap.keySet()) {
            list.add(treeMap.get(key));
        }

        return list;
    }

    private ArrayList<Day> mDays;

    public MonthlyViewAdapter(Context context, ArrayList<Day> arrayList) {
        mDays = arrayList;
    }

    public MonthlyViewAdapter(Context context, TreeMap<Calendar, Day> treeMap) {
        mDays = convert(treeMap);
    }

    public void setDataSet(ArrayList<Day> list) {
        mDays = list;
        notifyDataSetChanged();
    }

    public void setDataSet(TreeMap<Calendar, Day> treeMap) {
        mDays = convert(treeMap);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MonthlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DailyMonthlyBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.fragment_daily_monthly, parent, false);
        return new MonthlyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Day day = mDays.get(position);
        DailyMonthlyViewModel model = new DailyMonthlyViewModel();
        MonthlyViewHolder holder = (MonthlyViewHolder) viewHolder;
        model.setDay(day.getCalendar(), day.getEvents(), day.getIsThisMonth());
        holder.setViewModel(model);
    }

    @Override
    public int getItemCount() {
        return mDays.size();
    }
}
