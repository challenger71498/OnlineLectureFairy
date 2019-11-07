package com.example.onlinelecturefairy.event;

import com.example.onlinelecturefairy.R;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.common.collect.HashBiMap;

import java.util.Date;

public class CalendarEvent {
    public Event getMyEvent() {
        return myEvent;
    }

    public void setMyEvent(Event myEvent) {
        this.myEvent = myEvent;
    }

    private Event myEvent;

    public CalendarEvent(){
        myEvent = null;
    }

    public CalendarEvent(Event event){
        this();
        myEvent = event;
    }
}