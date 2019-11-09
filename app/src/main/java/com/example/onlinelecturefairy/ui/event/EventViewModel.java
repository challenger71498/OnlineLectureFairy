package com.example.onlinelecturefairy.ui.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.onlinelecturefairy.event.CalendarEvent;
import com.example.onlinelecturefairy.event.Event;

public class EventViewModel extends ViewModel {
    private MutableLiveData<CalendarEvent> mEvent;

    public void initEvent() {
        CalendarEvent event = new CalendarEvent();
        mEvent.setValue(event);
    }

    public void setEvent(CalendarEvent event) {
        mEvent = new MutableLiveData<>();
        mEvent.setValue(event);
    }

    public LiveData<CalendarEvent> getEvent() {
        if(mEvent == null) {
            mEvent = new MutableLiveData<>();
            loadEvent();
        }
        return mEvent;
    }

    private void loadEvent() {
        // Do an asynchronous operation to fetch event.
    }
}
