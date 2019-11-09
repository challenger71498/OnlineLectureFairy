package com.example.onlinelecturefairy.ui.monthly.dailymonthly;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.onlinelecturefairy.day.Day;
import com.example.onlinelecturefairy.event.CalendarEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DailyMonthlyViewModel extends ViewModel {
    private MutableLiveData<Day> mDay;

    public void initDay() {
        mDay = new MutableLiveData<>();
        mDay.setValue(new Day(new GregorianCalendar(), false));
    }

    public void setDay(Calendar calendar, ArrayList<CalendarEvent> events, boolean isThisMonth) {
        mDay = new MutableLiveData<>();
        mDay.setValue(new Day(calendar, events, isThisMonth));
    }

    public LiveData<Day> getDay() {
        if(mDay == null) {
            mDay = new MutableLiveData<>();
            loadDay();
        }
        return mDay;
    }

    private void loadDay() {
        // Do an asynchronous operation to fetch calendar.
    }
}
