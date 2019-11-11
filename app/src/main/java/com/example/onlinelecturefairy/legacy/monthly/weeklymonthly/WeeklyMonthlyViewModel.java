package com.example.onlinelecturefairy.legacy.monthly.weeklymonthly;

import com.example.onlinelecturefairy.legacy.day.Day;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WeeklyMonthlyViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Day>> mDays;
}
