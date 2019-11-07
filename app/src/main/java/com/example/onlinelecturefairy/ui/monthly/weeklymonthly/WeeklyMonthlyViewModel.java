package com.example.onlinelecturefairy.ui.monthly.weeklymonthly;

import com.example.onlinelecturefairy.day.Day;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WeeklyMonthlyViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Day>> mDays;
}
