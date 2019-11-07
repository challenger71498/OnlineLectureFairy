package com.example.onlinelecturefairy.ui.monthly;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.onlinelecturefairy.event.Event;

import java.util.Calendar;
import java.util.List;

public class DailyMonthlyViewModel extends ViewModel {
    private MutableLiveData<Calendar> mCalendar;
    private MutableLiveData<List<Event>> mEvents;

    public LiveData<Calendar> getCalendar() {
        if(mCalendar == null) {
            mCalendar = new MutableLiveData<>();
            loadCalendar();
        }
        return mCalendar;
    }

    private void loadCalendar() {
        // Do an asynchronous operation to fetch calendar.
        if(mEvents == null) {
            getEvents();
            loadCalendar();
        }
    }

    public LiveData<List<Event>> getEvents() {
        if(mEvents == null) {
            mEvents = new MutableLiveData<>();
            loadEvents();
        }

        return mEvents;
    }

    private void loadEvents() {
        // Do an asynchronous operation to fetch events.
    }
}
