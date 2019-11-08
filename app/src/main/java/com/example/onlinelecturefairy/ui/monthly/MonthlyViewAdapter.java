package com.example.onlinelecturefairy.ui.monthly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.databinding.FragmentDailyMonthlyBinding;
import com.example.onlinelecturefairy.event.CalendarEvent;
import com.example.onlinelecturefairy.ui.monthly.dailymonthly.DailyMonthlyViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class MonthlyViewAdapter extends RecyclerView.Adapter {
    class DataSet {
        public Calendar calendar;
        public ArrayList<CalendarEvent> events;

        public DataSet(Calendar calendar, ArrayList<CalendarEvent> events) {
            this.calendar = calendar;
            this.events = events;
        }
    }

    private ArrayList<DataSet> convert(TreeMap<Calendar, ArrayList<CalendarEvent>> treeMap) {
        ArrayList<DataSet> list = new ArrayList<>();
        for(Calendar key : treeMap.keySet()) {
            list.add(new DataSet(key, treeMap.get(key)));
        }

        return list;
    }

    private ArrayList<DataSet> mDataSet;

    public MonthlyViewAdapter(Context context, ArrayList<DataSet> arrayList) {
        mDataSet = arrayList;
    }

    public MonthlyViewAdapter(Context context, TreeMap<Calendar, ArrayList<CalendarEvent>> treeMap) {
        mDataSet = convert(treeMap);
    }

    public void setDataSet(ArrayList<DataSet> list) {
        mDataSet = list;
        notifyDataSetChanged();
    }

    public void setDataSet(TreeMap<Calendar, ArrayList<CalendarEvent>> treeMap) {
        mDataSet = convert(treeMap);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MonthlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentDailyMonthlyBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.fragment_daily_monthly, parent, false);
        return new MonthlyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        DataSet dataSet = mDataSet.get(position);
        DailyMonthlyViewModel model = new DailyMonthlyViewModel();
        MonthlyViewHolder holder = (MonthlyViewHolder) viewHolder;
        model.setDay(dataSet.calendar, dataSet.events);
        holder.setViewModel(model);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
