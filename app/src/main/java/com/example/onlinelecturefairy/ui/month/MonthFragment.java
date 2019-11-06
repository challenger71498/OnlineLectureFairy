package com.example.onlinelecturefairy.ui.month;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.ui.decorator.EventDecorator;
import com.example.onlinelecturefairy.ui.decorator.TodayDecorator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

public class MonthFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_month, container, false);

        MaterialCalendarView materialCalendarView = view.findViewById(R.id.calendar);
        materialCalendarView.addDecorators(
                new EventDecorator(getActivity()),
                new TodayDecorator(getActivity())
        );

        return view;
    }
}
