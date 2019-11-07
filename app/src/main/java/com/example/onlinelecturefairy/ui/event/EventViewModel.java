package com.example.onlinelecturefairy.ui.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.onlinelecturefairy.event.Event;

public class EventViewModel extends ViewModel {
    private MutableLiveData<Event> event;

    public LiveData<Event> getEvent() {
        if(event == null) {
            event = new MutableLiveData<>();
            loadEvent();
        }
        return event;
    }

    private void loadEvent() {
        // Do an asynchronous operation to fetch event.
    }
}
