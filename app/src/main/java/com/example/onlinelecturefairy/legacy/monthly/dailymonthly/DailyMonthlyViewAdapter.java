package com.example.onlinelecturefairy.legacy.monthly.dailymonthly;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.databinding.FragmentEventBinding;
import com.example.onlinelecturefairy.event.CalendarEvent;
import com.example.onlinelecturefairy.legacy.event.EventViewModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class DailyMonthlyViewAdapter extends RecyclerView.Adapter {
    private ArrayList<CalendarEvent> mEvents;

    public DailyMonthlyViewAdapter(ArrayList<CalendarEvent> events) {
        this.mEvents = events;
    }

    public void setmEvents(ArrayList<CalendarEvent> events) {
        this.mEvents = events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DailyMonthlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentEventBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.fragment_event, parent, false);
        return new DailyMonthlyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        CalendarEvent event = mEvents.get(position);
        EventViewModel model = new EventViewModel();
        DailyMonthlyViewHolder holder = (DailyMonthlyViewHolder) viewHolder;
        model.setEvent(event);
        holder.setViewModel(model);
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }
}
