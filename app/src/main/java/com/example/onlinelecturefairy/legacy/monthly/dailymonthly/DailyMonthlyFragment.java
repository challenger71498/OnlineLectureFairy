package com.example.onlinelecturefairy.legacy.monthly.dailymonthly;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinelecturefairy.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DailyMonthlyFragment extends Fragment {
    private DailyMonthlyViewModel model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.daily_monthly, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model = ViewModelProviders.of(this).get(DailyMonthlyViewModel.class);

        model.getDay().observe(this, day -> {
            // update UI;
            RecyclerView v = getView().findViewById(R.id.dailyView);
            DailyMonthlyViewAdapter adapter = (DailyMonthlyViewAdapter) v.getAdapter();
            if(adapter != null) {
                adapter.setmEvents(day.getEvents());
            } else {
                LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                adapter = new DailyMonthlyViewAdapter(day.getEvents());
                v.setAdapter(adapter);
                v.setLayoutManager(manager);
            }
        });

        if(model != null) {
            model.initDay();
        }
    }
}
