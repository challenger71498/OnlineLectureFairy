package com.example.onlinelecturefairy.ui.monthly.dailymonthly;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.databinding.FragmentDailyMonthlyBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class DailyMonthlyFragment extends Fragment {
    private FragmentDailyMonthlyBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_monthly, container, false);

        binding = DataBindingUtil.setContentView(Objects.requireNonNull(getActivity()), R.layout.fragment_daily_monthly);

        DailyMonthlyViewModel model = ViewModelProviders.of(this).get(DailyMonthlyViewModel.class);
        model.getDay().observe(this, day -> {
            // update UI;
            Calendar today = Calendar.getInstance();
            Calendar date = day.getCalendar();

            // update date.
            binding.setDate(new SimpleDateFormat("d", Locale.KOREAN).format(date));

            // checks whether today or not.
            binding.setIsToday(
                    date.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                    && date.get(Calendar.YEAR) == today.get(Calendar.YEAR)
            );
        });

        return view;
    }

}
