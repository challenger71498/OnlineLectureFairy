package com.example.onlinelecturefairy.event;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

import java.util.Date;

public class TodoEvent extends CalendarEvent {
    private boolean isDone;

    public TodoEvent(Date start, Date end, Event event) {
        super(start, end, event);
    }

    public TodoEvent(DateTime start, DateTime end, Event event) {
        super(start, end, event);

        isDone = false;
    }
}
