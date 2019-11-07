package com.example.onlinelecturefairy.ui.monthly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.common.BindingRecyclerViewAdapter;
import com.example.onlinelecturefairy.event.Event;

import java.util.List;

public class DailyMonthlyViewAdapter extends BindingRecyclerViewAdapter<Event, DailyMonthlyViewHolder> {

    public DailyMonthlyViewAdapter(Context context) {
        super(context);
    }

    public DailyMonthlyViewAdapter(Context context, List arrayList) {
        super(context, arrayList);
    }

    @Override
    public void onBindView(DailyMonthlyViewHolder holder, int position) {
        Event event = getItem(position);
        holder.binding.setEvent(event);
    }

    @NonNull
    @Override
    public DailyMonthlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_event, parent, false);
        return new DailyMonthlyViewHolder(view);
    }
}
